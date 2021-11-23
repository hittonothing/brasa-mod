package com.dtlivehero.brasa.common.block.fusion_chamber;

import com.dtlivehero.brasa.common.ModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class FusionChamberBE extends BlockEntity implements MenuProvider {

    public FusionChamberBE(BlockPos blockPos, BlockState blockState) {
        super(ModCommon.FUSION_CHAMBER_BE.get(), blockPos, blockState);

    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int index, Inventory inventory, Player player) {
        return null;
    }
}
