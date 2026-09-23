package net.shirojr.titanfabric.compat.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import net.minecraft.util.Identifier;

import java.util.List;

public class EffectSmithingEmiRecipe extends EmiSmithingRecipe {
    private final EmiStack recipeIndex;

    public EffectSmithingEmiRecipe(EmiIngredient input, EmiIngredient addition, EmiStack output, EmiStack recipeIndex, Identifier id) {
        super(EmiStack.EMPTY, input, addition, output, id);
        this.recipeIndex = recipeIndex;
    }

    @Override
    public List<EmiStack> getOutputs() {
        if (output.getKey().equals(recipeIndex.getKey())) {
            return super.getOutputs();
        }
        return List.of(output, recipeIndex);
    }

    @Override
    public boolean supportsRecipeTree() {
        return output.getKey().equals(recipeIndex.getKey());
    }
}
