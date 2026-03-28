package net.shirojr.titanfabric.compat.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;

import java.util.List;

public class GrindstoneEffectRemovalRecipe implements EmiRecipe {
    private static final Identifier BACKGROUND = EmiPort.id("minecraft", "textures/gui/container/grindstone.png");

    private final EmiStack input;
    private final EmiStack output;
    private final Identifier id;

    public GrindstoneEffectRemovalRecipe(EmiStack input, EmiStack output, Identifier id) {
        this.input = input;
        this.output = output;
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.GRINDING;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public int getDisplayWidth() {
        return 116;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, 116, 56, 30, 15);
        widgets.addSlot(input, 18, 3).drawBack(false);
        widgets.addSlot(18, 24).drawBack(false);
        widgets.addSlot(output, 98, 18).drawBack(false).recipeContext(this);
    }
}
