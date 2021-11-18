package com.dtlivehero.brasa;

import com.dtlivehero.brasa.common.ModBlocks;
import com.dtlivehero.brasa.common.ModCommon;
import com.dtlivehero.brasa.common.ModItems;
import com.dtlivehero.brasa.common.ModRecipes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BrasaRegister {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Brasa.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Brasa.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Brasa.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Brasa.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Brasa.MOD_ID);

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENU_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);

        ModBlocks.register();
        ModItems.register();
        ModCommon.register();
        ModRecipes.register();
    }

    public static final CreativeModeTab BRASA_TAB = new CreativeModeTab("brasa_stuff") {
        @Override
        public ItemStack makeIcon() {
            return ModBlocks.PEDRITA_BLOCK.get().asItem().getDefaultInstance();
        }
    };
}
