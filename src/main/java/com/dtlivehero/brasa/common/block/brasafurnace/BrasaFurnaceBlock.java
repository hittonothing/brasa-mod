package com.dtlivehero.brasa.common.block.brasafurnace;

import com.dtlivehero.brasa.common.ModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;

public class BrasaFurnaceBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public BrasaFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties
                .strength(3, 15)
                .lightLevel(BrasaFurnaceBlock::getLightValue)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.FALSE));
    }

    public static int getLightValue(BlockState state) {
        int lightValue = 0;
        Boolean isBurning = state.getValue(LIT);

        if(isBurning) {
            lightValue = 15;
        }
        return lightValue;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BrasaFurnaceBE(blockPos, blockState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) return InteractionResult.SUCCESS;
        MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
        if(menuProvider != null) {
            if(!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.FAIL;
            NetworkHooks.openGui(serverPlayer, menuProvider, (packetBuffer) -> {});
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof BrasaFurnaceBE brasaFurnaceBE) {
            if(brasaFurnaceBE.getStoredExp() > 0) {
                brasaFurnaceBE.createExperience(level, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newEstate, boolean isMoving) {
        if(state.getBlock() != newEstate.getBlock()) {
            BlockEntity blockEntity = levelIn.getBlockEntity(pos);
            if(blockEntity instanceof BrasaFurnaceBE brasaFurnaceBE) {
                brasaFurnaceBE.dropAllContents(levelIn, pos);
            }
        }
        super.onRemove(state, levelIn, pos, newEstate, isMoving);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, LIT);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> entityType) {
        return level.isClientSide ? null : createTickerHelper(entityType, ModCommon.BRASA_FURNACE_BE.get(), BrasaFurnaceBE::tick);
    }
}
