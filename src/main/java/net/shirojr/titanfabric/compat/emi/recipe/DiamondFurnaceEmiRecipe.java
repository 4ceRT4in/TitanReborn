package net.shirojr.titanfabric.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.util.TitanFabricTags;

import java.util.Arrays;
import java.util.List;

public class DiamondFurnaceEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final EmiRecipeCategory category;
    private final EmiIngredient input;
    private final EmiStack output;
    private final AbstractCookingRecipe recipe;
    private final int fuelMultiplier;
    private final boolean infiniBurn;

    public DiamondFurnaceEmiRecipe(AbstractCookingRecipe recipe, EmiRecipeCategory category, int fuelMultiplier, boolean infiniBurn) {
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + "/diamondfurnacerecipe");
        this.category = category;
        input = EmiIngredient.of(recipe.getIngredients().get(0));
        boolean isBetterSmeltingItem = Arrays.stream(recipe.getIngredients().get(0).getMatchingStacks())
                .anyMatch(stack -> stack.isIn(TitanFabricTags.Items.BETTER_SMELTING_ITEMS));
        var outputStack = recipe.getOutput();

        if (isBetterSmeltingItem) {
            outputStack.setCount(recipe.getOutput().getCount() * 2);
        }

        output = EmiStack.of(outputStack);
        if (input.getEmiStacks().get(0).getItemStack().isOf(Items.WET_SPONGE)) {
            input.getEmiStacks().get(0).setRemainder(EmiStack.of(Fluids.WATER, 81_000));
        }
        this.recipe = recipe;
        this.fuelMultiplier = fuelMultiplier;
        this.infiniBurn = infiniBurn;
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
        if (infiniBurn) {
            widgets.addTexture(EmiTexture.FULL_FLAME, 1, 24);
        } else {
            widgets.addTexture(EmiTexture.EMPTY_FLAME, 1, 24);
            widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 1, 24, 4000 / fuelMultiplier, false, true, true);
        }
        widgets.addSlot(input, 0, 4);
        widgets.addSlot(output, 56, 0).output(true).recipeContext(this);
    }
}
