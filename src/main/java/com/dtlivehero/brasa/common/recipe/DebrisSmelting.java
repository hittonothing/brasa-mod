package dtlivehero.brasa.common.recipe;

import com.google.gson.JsonObject;
import dtlivehero.brasa.common.ModRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class DebrisSmelting extends AbstractCookingRecipe {

    private final ResourceLocation recipeId;

    public DebrisSmelting(ResourceLocation recipeId, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(ModRecipes.Types.DEBRIS_SMELTING_RECIPE_TYPE, recipeId, "", ingredient, result, experience, cookingTime);
        this.recipeId = recipeId;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43743_, int p_43744_) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.Serializers.DEBRIS_SMELTING.get();
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public float getExperience() {
        return this.experience;
    }

    @Override
    public int getCookingTime() {
        return this.cookingTime;
    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.Types.DEBRIS_SMELTING_RECIPE_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<DebrisSmelting> {

        @Override
        public DebrisSmelting fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

            ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(json, "result"));
            ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemId));

            float experience = GsonHelper.getAsFloat(json, "experience");
            int cookingTime = GsonHelper.getAsInt(json, "cookingTime");
            return new DebrisSmelting(recipeId, ingredient, result, experience, cookingTime);
        }

        @Nullable
        @Override
        public DebrisSmelting fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            float experience = buffer.readFloat();
            int cookingTime = buffer.readInt();
            return new DebrisSmelting(recipeId, ingredient, result, experience, cookingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DebrisSmelting recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeFloat(recipe.experience);
            buffer.writeInt(recipe.cookingTime);
        }
    }
}
