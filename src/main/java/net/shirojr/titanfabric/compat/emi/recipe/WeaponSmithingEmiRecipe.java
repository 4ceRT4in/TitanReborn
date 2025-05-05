package net.shirojr.titanfabric.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.recipe.custom.old.WeaponRecipe;

import java.util.List;

public class WeaponSmithingEmiRecipe implements EmiRecipe {
    private static final EmiRecipeCategory category = VanillaEmiRecipeCategories.SMITHING;

    private final WeaponRecipe recipe;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;


    public WeaponSmithingEmiRecipe(WeaponRecipe recipe, ItemStack baseStack, ItemStack baseEssence, String additionalPath) {
        this.recipe = recipe;
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + additionalPath);
        this.inputs = List.of(EmiStack.of(baseStack), EmiStack.of(baseEssence));
        this.outputs = List.of(EmiStack.of(recipe.getFakedOutput(baseStack, baseEssence)));
    }

    public WeaponSmithingEmiRecipe(WeaponRecipe recipe, ItemStack baseEssence, String additionalPath) {
        this.recipe = recipe;
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + additionalPath);
        this.inputs = List.of(EmiIngredient.of(recipe.base), EmiStack.of(baseEssence));
        this.outputs = List.of(EmiStack.of(recipe.getFakedOutput(baseEssence)));
    }


    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add input slots

        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addSlot(inputs.get(1), 49, 0);
        widgets.addSlot(outputs.get(0), 107, 0).recipeContext(this);

    }
}
