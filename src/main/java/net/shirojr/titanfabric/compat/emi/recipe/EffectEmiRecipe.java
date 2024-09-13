package net.shirojr.titanfabric.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

import java.util.List;

public class EffectEmiRecipe implements EmiRecipe {
    private final EffectRecipe recipe;
    private final EmiRecipeCategory category;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public EffectEmiRecipe(EffectRecipe recipe, EmiRecipeCategory category, WeaponEffect effect, String additionalPath) {
        this.recipe = recipe;
        this.category = category;
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + additionalPath);

        this.inputs = List.of(
                EmiIngredient.of(recipe.base.ingredient()),
                EmiStack.of(EffectHelper.getPotionFromWeaponEffect(effect))
        );

        this.outputs = List.of(EmiStack.of(recipe.getFakedOutput(effect)));
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
        // Add input slots
        widgets.addSlot(0, 0);
        widgets.addSlot((2 % 3) * 18, 0);
        widgets.addSlot(0, (6 / 3) * 18);
        widgets.addSlot((8 % 3) * 18, (8 / 3) * 18);
        for (int i = 0; i < recipe.base.slots().length; i++) {
            int slot = recipe.base.slots()[i];
            widgets.addSlot(inputs.get(0), (slot % 3) * 18, (slot / 3) * 18);
        }
        for (int i = 0; i < recipe.effectModifier.slots().length; i++) {
            int slot = recipe.effectModifier.slots()[i];
            widgets.addSlot(inputs.get(1), (slot % 3) * 18, (slot / 3) * 18);
        }

        // Add arrow
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 18);

        // Add output slot
        widgets.addSlot(outputs.get(0), 126, 18).recipeContext(this);
    }
}
