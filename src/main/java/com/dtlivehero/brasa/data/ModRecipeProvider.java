package com.dtlivehero.brasa.data;

import com.dtlivehero.brasa.common.ModBlocks;
import com.dtlivehero.brasa.common.ModItems;
import com.dtlivehero.brasa.data.builder.BrasaRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(ModItems.PEDRITA_DEBRIS.get(), 9)
                .requires(ModBlocks.PEDRITA_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.PEDRITA_BLOCK.get()))
                .save(consumer);

        BrasaRecipeBuilder.smeltingDebris(ModItems.FERRILUME_NUGGET.get())
                .requires(ModItems.PEDRITA_DEBRIS.get())
                .experience(2.0f)
                .cookingTime(400)
                .save(consumer);
        BrasaRecipeBuilder.smeltingDebris(ModItems.FERRILUME_INGOT.get())
                .requires(ModBlocks.PEDRITA_BLOCK.get())
                .experience(2.0f)
                .cookingTime(400)
                .save(consumer);
    }
}
