package net.shirojr.titanfabric.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.shirojr.titanfabric.recipe.custom.DyeableRecipe;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

import java.util.ArrayList;
import java.util.List;

public class DyeableEmiRecipe implements EmiRecipe {
    private final DyeableRecipe recipe;
    private final EmiRecipeCategory category;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;


    public DyeableEmiRecipe(DyeableRecipe recipe, EmiRecipeCategory category, String additionalPath) {
        this.recipe = recipe;
        this.category = category;
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + additionalPath);

        DefaultedList<Ingredient> recipeIngredients = recipe.getIngredients();

        this.inputs = new ArrayList<>();
        for (Ingredient ingredient : recipeIngredients) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }

        this.outputs = List.of(EmiStack.of(recipe.getOutput()));
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
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }


    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add first input slot (item to dye)
        widgets.addSlot(inputs.get(0), 0, 9);

        // Add second input slot (dye)
        widgets.addSlot(inputs.get(1), 27, 9);

        // Add arrow
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 54, 9);

        // Add output slot
        widgets.addSlot(outputs.get(0), 96, 9).recipeContext(this);
    }
}
