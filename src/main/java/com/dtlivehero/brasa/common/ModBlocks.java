package com.dtlivehero.brasa.common;

import com.dtlivehero.brasa.BrasaRegister;
import com.dtlivehero.brasa.common.block.brasafurnace.BrasaFurnaceBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final RegistryObject<Block> PEDRITA_BLOCK = register("pedrita_block", () -> new Block(BlockBehaviour.Properties.of(
            Material.STONE)
            .strength(3, 15)
            .requiresCorrectToolForDrops()
    ));

    public static final RegistryObject<BrasaFurnaceBlock> BRASA_FURNACE_BLOCK = register("brasa_furnace_block", () -> new BrasaFurnaceBlock(BlockBehaviour.Properties.of(Material.METAL)));

    public static void register() {}

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return BrasaRegister.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> registerBlock = registerBlock(name, block);
        BrasaRegister.ITEMS.register(name, () -> new BlockItem(registerBlock.get(), new Item.Properties().tab(BrasaRegister.BRASA_TAB)));
        return registerBlock;
    }
}
