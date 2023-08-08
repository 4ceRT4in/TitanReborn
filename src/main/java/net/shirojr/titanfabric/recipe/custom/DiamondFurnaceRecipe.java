package net.shirojr.titanfabric.recipe.custom;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.TitanFabricTags;

public class DiamondFurnaceRecipe extends AbstractCookingRecipe {
    public DiamondFurnaceRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(type, id, group, input, output, experience, cookTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMELTING;
    }

    @Override
    public ItemStack createIcon() {
        return TitanFabricItems.LEGEND_INGOT.getDefaultStack();
    }

    @Override
    public int getCookTime() {
        return super.getCookTime() / 2;
    }

    @Override
    public ItemStack getOutput() {
        ItemStack outputStack = super.getOutput().copy();
        if (outputStack.isIn(TitanFabricTags.Items.ORES)) outputStack.setCount(outputStack.getCount() * 2);
        return outputStack;
    }
}
