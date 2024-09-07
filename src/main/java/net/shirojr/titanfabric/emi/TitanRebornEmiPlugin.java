package net.shirojr.titanfabric.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.recipe.custom.MultiBowRecipe;
import net.shirojr.titanfabric.recipe.custom.WeaponRecipe;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanRebornEmiPlugin implements EmiPlugin {
    public static final Identifier TEXTURE = new Identifier("titanfabric", "textures/gui/emi_recipes.png");
    public static final EmiStack WORKSTATION = EmiStack.of(TitanFabricItems.LEGEND_GREATSWORD);
    public static final EmiRecipeCategory EFFECT_CATEGORY = new EmiRecipeCategory(
            new Identifier("titanfabric", "effect_recipes"),
            WORKSTATION,
            new EmiTexture(TEXTURE, 0, 0, 16, 16)
    );
    public static final EmiRecipeCategory WEAPON_CATEGORY = new EmiRecipeCategory(
            new Identifier("titanfabric", "weapon_recipes"),
            WORKSTATION,
            new EmiTexture(TEXTURE, 16, 0, 16, 16)
    );
    public static final EmiRecipeCategory MULTI_BOW_CATEGORY = new EmiRecipeCategory(
            new Identifier("titanfabric", "multi_bow_recipes"),
            WORKSTATION,
            new EmiTexture(TEXTURE, 32, 0, 16, 16)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EFFECT_CATEGORY);
        registry.addWorkstation(EFFECT_CATEGORY, WORKSTATION);

        RecipeManager recipeManager = registry.getRecipeManager();
        TitanFabric.LOGGER.info("Registering TitanReborn custom EMI recipes");

        //List<EffectRecipe> effectRecipes = recipeManager.listAllOfType(TitanFabricRecipes.WEAPON_EFFECT_RECIPE_TYPE);
        //TitanFabric.LOGGER.info("Found " + effectRecipes.size() + " Effect Recipes.");
        //0 found?????
        /*for (EffectRecipe recipe : effectRecipes) {
            registry.addRecipe(new EffectEmiRecipe(recipe, EFFECT_CATEGORY));
            TitanFabric.LOGGER.info("Registered EMI recipe: " + recipe.getId());
        }*/

        TitanFabric.LOGGER.info("Done registering TitanReborn custom EMI recipes");
    }
}

class EffectEmiRecipe implements EmiRecipe {
    private final EffectRecipe recipe;
    private final EmiRecipeCategory category;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public EffectEmiRecipe(EffectRecipe recipe, EmiRecipeCategory category) {
        this.recipe = recipe;
        this.category = category;
        this.id = recipe.getId();

        this.inputs = List.of(
                EmiIngredient.of(recipe.base.ingredient()),
                EmiIngredient.of(recipe.effectModifier.ingredient())
        );

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
        // Add input slots
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

