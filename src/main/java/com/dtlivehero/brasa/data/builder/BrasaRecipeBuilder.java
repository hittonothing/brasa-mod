package com.dtlivehero.brasa.data.builder;

import com.dtlivehero.brasa.common.ModRecipes;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.dtlivehero.brasa.Brasa;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class BrasaRecipeBuilder {

    private List<Ingredient> ingredients = Lists.newArrayList();
    private final Item result;
    private float experience;
    private int cookingTime;
    private String group;

    public BrasaRecipeBuilder(ItemLike result) {
        this.result = result.asItem();
    }

    public static BrasaRecipeBuilder smeltingDebris(ItemLike result) {
        return new BrasaRecipeBuilder(result);
    }

    public BrasaRecipeBuilder requires(ItemLike required) {
        return this.requires(Ingredient.of(required));
    }

    public BrasaRecipeBuilder requires(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public BrasaRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    public BrasaRecipeBuilder cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public BrasaRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, new ResourceLocation(Brasa.MOD_ID, "debris_smelting_" + this.result.asItem()));
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation key) {
        consumer.accept(new BrasaRecipeBuilder.Result(key, this.result, this.group == null ? "" : this.group, this.ingredients, this.experience, this.cookingTime));
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final Item result;
        private final float experience;
        private final int cookingTime;
        private final String group;
        private final List<Ingredient> ingredient;

        public Result(ResourceLocation id, Item result, String group, List<Ingredient> ingredient, float experience, int cookingTime) {
            this.id = id;
            this.result = result;
            this.group = group;
            this.ingredient = ingredient;
            this.experience = experience;
            this.cookingTime = cookingTime;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if(!group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            JsonArray jsonArray = new JsonArray();

            for(Ingredient ingredient : this.ingredient) {
                jsonArray.add(ingredient.toJson());
            }

            json.add("ingredient", jsonArray);

            json.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
            json.addProperty("experience", this.experience);
            json.addProperty("cookingTime", this.cookingTime);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.Serializers.DEBRIS_SMELTING.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
