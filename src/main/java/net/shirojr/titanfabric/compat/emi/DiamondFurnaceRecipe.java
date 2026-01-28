package net.shirojr.titanfabric.compat.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricTags;

import java.util.List;

public class DiamondFurnaceRecipe implements EmiRecipe {
    private final Identifier id;
    private final EmiIngredient input;
    private final EmiStack output;
    private final AbstractCookingRecipe recipe;

    public DiamondFurnaceRecipe(AbstractCookingRecipe recipe) {
        this.id = TitanFabric.getId("/" + EmiPort.getId(recipe).getPath() + "_diamond_furnace");
        input = EmiIngredient.of(recipe.getIngredients().get(0));
        ItemStack outputStack = EmiPort.getOutput(recipe);

        if (!input.getEmiStacks().isEmpty() && input.getEmiStacks().get(0).getItemStack().isIn(TitanFabricTags.Items.BETTER_SMELTING_ITEMS)) {
            outputStack.setCount(2);
        }
        output = EmiStack.of(outputStack);
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return TitanRebornEmiPlugin.DIAMOND_FURNACE_CATEGORY;
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
    public int getDisplayWidth() {
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addFillingArrow(24, 5, 50 * recipe.getCookingTime() / 2).tooltip((mx, my) -> {
            return List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("emi.cooking.time", recipe.getCookingTime() / 20f))));
        });
        widgets.addTexture(EmiTexture.EMPTY_FLAME, 1, 24);
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 1, 24, 8000, false, true, true);

        widgets.addText(EmiPort.ordered(EmiPort.translatable("emi.cooking.experience", recipe.getExperience())), 26, 28, -1, true);
        widgets.addSlot(input, 0, 4);
        widgets.addSlot(output, 56, 0).large(true).recipeContext(this);
    }
}

