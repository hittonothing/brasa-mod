package com.dtlivehero.brasa.common;

import com.dtlivehero.brasa.BrasaRegister;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.function.Supplier;

public class ModItems {

    public static final RegistryObject<Item> PEDRITA_DEBRIS = register("pedrita_debris", () -> new Item(new Item.Properties().tab(BrasaRegister.BRASA_TAB)));
    public static final RegistryObject<Item> FERRILUME_NUGGET = register("ferrilume_nugget", () -> new Item(new Item.Properties().tab(BrasaRegister.BRASA_TAB)));
    public static final RegistryObject<Item> FERRILUME_INGOT = register("ferrilume_ingot", () -> new Item(new Item.Properties().tab(BrasaRegister.BRASA_TAB)));


    public static void register() {}

    public static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return BrasaRegister.ITEMS.register(name, item);
    }
}
