package com.dtlivehero.brasa.common.block.fuel_can;

import com.dtlivehero.brasa.common.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class FuelCanBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;

    public static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 16, 11);

    public FuelCanBlock() {
        super(Properties.of(Material.METAL)
                .strength(3, 15)
                .requiresCorrectToolForDrops());
        this.defaultBlockState().setValue(WATERLOGGED, false);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();

        FluidState fluidLevelOfCurrentBlock = context.getLevel().getFluidState(context.getClickedPos());
        boolean blockContainsWater = fluidLevelOfCurrentBlock.getType() == Fluids.WATER;

        BlockState blockState = defaultBlockState().setValue(WATERLOGGED, blockContainsWater);
        blockState = setConnections(level, blockPos, blockState);
        return blockState;
    }

    private BlockState setConnections(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return blockState.setValue(WEST, attachToNeighbourThisDirection(blockGetter, blockPos, Direction.WEST))
                .setValue(EAST, attachToNeighbourThisDirection(blockGetter, blockPos, Direction.EAST))
                .setValue(NORTH, attachToNeighbourThisDirection(blockGetter, blockPos, Direction.NORTH))
                .setValue(SOUTH, attachToNeighbourThisDirection(blockGetter, blockPos, Direction.SOUTH));
    }

    private boolean attachToNeighbourThisDirection(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        BlockPos neighbourPos = blockPos.offset(direction.getNormal());
        BlockState neighbourBlockState = blockGetter.getBlockState(neighbourPos);
        Block neighbourBlock = neighbourBlockState.getBlock();

        if(neighbourBlock == Blocks.BARRIER) return false;
        if(neighbourBlock == ModBlocks.FUSION_CHAMBER_BLOCK.get()) return true;
        return neighbourBlockState.isFaceSturdy(blockGetter, blockPos, direction.getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED, WEST, EAST, NORTH, SOUTH);
    }
}
