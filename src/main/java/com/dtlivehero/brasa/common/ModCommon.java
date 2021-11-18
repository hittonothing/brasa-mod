package com.dtlivehero.brasa.common;

import com.dtlivehero.brasa.BrasaRegister;
import com.dtlivehero.brasa.common.block.brasafurnace.BrasaFurnaceBE;
import com.dtlivehero.brasa.common.block.brasafurnace.BrasaFurnaceContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmllegacy.network.IContainerFactory;

public class ModCommon {

    public static final RegistryObject<BlockEntityType<BrasaFurnaceBE>> BRASA_FURNACE_BE = registerBE("brasa_furnace_be", BrasaFurnaceBE::new, ModBlocks.BRASA_FURNACE_BLOCK);
    public static final RegistryObject<MenuType<BrasaFurnaceContainer>> BRASA_FURNACE_CONTAINER = registerContainer("brasa_furnace_container", BrasaFurnaceContainer::createContainerClientSide);

    public static void register() {}

    private static <I extends AbstractContainerMenu> RegistryObject<MenuType<I>> registerContainer(String name, IContainerFactory<I> factory) {
        return BrasaRegister.MENU_TYPES.register(name, () -> IForgeContainerType.create(factory));
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBE(String name, BlockEntityType.BlockEntitySupplier<T> factory, RegistryObject<? extends Block> block) {
        return BrasaRegister.BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }
}
