package com.dtlivehero.brasa.common;

import com.dtlivehero.brasa.BrasaRegister;
import com.dtlivehero.brasa.common.recipe.DebrisSmelting;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fmllegacy.RegistryObject;

public class ModRecipes {

    public static final class Types {

        public static final RecipeType<DebrisSmelting> DEBRIS_SMELTING_RECIPE_TYPE = RecipeType.register(":debris_smelting");

        private Types() {}

        public static void register() {}
    }

    public static final class  Serializers {

        public static final RegistryObject<RecipeSerializer<?>> DEBRIS_SMELTING = BrasaRegister.RECIPE_SERIALIZERS.register("debris_smelting", DebrisSmelting.Serializer::new);

        private Serializers() {}

        public static void register() {}
    }

    public static void register() {
        Types.register();
        Serializers.register();
    }
}
