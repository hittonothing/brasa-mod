package com.dtlivehero.brasa.common.block.fuel_can;

import com.dtlivehero.brasa.common.ModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FuelCanBE extends BlockEntity {

    public FuelCanBE(BlockPos blockPos, BlockState blockState) {
        super(ModCommon.FUEL_CAN_BE.get(), blockPos, blockState);

    }
}
