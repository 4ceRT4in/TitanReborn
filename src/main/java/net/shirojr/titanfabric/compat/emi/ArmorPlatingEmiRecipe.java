package net.shirojr.titanfabric.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class ArmorPlatingEmiRecipe implements EmiRecipe {
    private final EmiStack armor;
    private final EmiStack plating;
    private final EmiStack output;
    private final Identifier id;

    public ArmorPlatingEmiRecipe(ItemStack armor, ItemStack plating, ItemStack output, Identifier id) {
        this.armor = EmiStack.of(armor);
        this.plating = EmiStack.of(plating);
        this.output = EmiStack.of(output);
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return TitanRebornEmiPlugin.ARMOR_PLATING_CATEGORY;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(this.armor, this.plating);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.output);
    }

    @Override
    public int getDisplayWidth() {
        return 105;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.armor, 0, 0);
        widgets.addTexture(EmiTexture.PLUS, 20, 5);
        widgets.addSlot(this.plating, 30, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 57, 1);
        widgets.addSlot(this.output, 87, 0).recipeContext(this);
    }
}
