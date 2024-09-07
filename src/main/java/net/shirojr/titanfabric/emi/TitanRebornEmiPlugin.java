package net.shirojr.titanfabric.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.recipe.custom.WeaponRecipe;
import net.shirojr.titanfabric.util.TitanFabricTags;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TitanRebornEmiPlugin implements EmiPlugin {

    public static final Identifier TEXTURE = new Identifier("titanfabric", "textures/gui/emi_recipes.png");
    public static final EmiStack DIAMOND_FURNACE_WORKSTATION = EmiStack.of(TitanFabricBlocks.DIAMOND_FURNACE);
    public static final EmiStack NETHERITE_ANVIL_WORKSTATION = EmiStack.of(TitanFabricBlocks.NETHERITE_ANVIL);
    public static final EmiRecipeCategory DIAMOND_FURNACE_CATEGORY = new EmiRecipeCategory(
            new Identifier("titanfabric", "diamond_furnace_recipes"),
            DIAMOND_FURNACE_WORKSTATION,
            new EmiTexture(TEXTURE, 0, 0, 16, 16)
    );

    public static final EmiRecipeCategory NETHERITE_ANVIL_CATEGORY = new EmiRecipeCategory(
            new Identifier("titanfabric", "netherite_anvil_recipes"),
            NETHERITE_ANVIL_WORKSTATION,
            new EmiTexture(TEXTURE, 0, 0, 16, 16)
    );

    @Override
    public void register(EmiRegistry registry) {

        // Tell EMI to add a tab for your category
        registry.addCategory(DIAMOND_FURNACE_CATEGORY);
        registry.addCategory(NETHERITE_ANVIL_CATEGORY);

        // Add all the workstations your category uses
        registry.addWorkstation(DIAMOND_FURNACE_CATEGORY, DIAMOND_FURNACE_WORKSTATION);
        registry.addWorkstation(NETHERITE_ANVIL_CATEGORY, NETHERITE_ANVIL_WORKSTATION);

        RecipeManager recipeManager = registry.getRecipeManager();
        TitanFabric.LOGGER.info("Registering TitanReborn custom EMI recipes");
        var recipes = recipeManager.values();
        for (var recipe : recipes) {
            if(recipe instanceof EffectRecipe) {
                EffectRecipe effectRecipe = (EffectRecipe) recipe;

                registry.addRecipe(new EffectEmiRecipe((EffectRecipe) recipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.BLIND, "blind_upgrade"));

                if(!effectRecipe.base.ingredient().test(new ItemStack(Items.ARROW))){
                    registry.addRecipe(new EffectEmiRecipe((EffectRecipe) recipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.FIRE, "fire_upgrade"));
                }
                registry.addRecipe(new EffectEmiRecipe((EffectRecipe) recipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.POISON, "poison_upgrade"));
                registry.addRecipe(new EffectEmiRecipe((EffectRecipe) recipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.WITHER, "wither_upgrade"));
                registry.addRecipe(new EffectEmiRecipe((EffectRecipe) recipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.WEAK, "weak_upgrade"));

                TitanFabric.LOGGER.info("Registered EMI recipe: " + recipe.getId());
            }
            if(recipe instanceof WeaponRecipe) {
                WeaponRecipe weaponRecipe = (WeaponRecipe) recipe;
                for (WeaponEffect entry : EffectHelper.getAllPossibleEffects(weaponRecipe.getOutput().getItem())) {
                    WeaponEffectData data = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, entry, 1);
                    ItemStack effectStack = EffectHelper.applyEffectToStack(new ItemStack(TitanFabricItems.ESSENCE), data);
                    var emiSwordSmithingRecipeBase = new WeaponSmithingEmiRecipe(weaponRecipe, VanillaEmiRecipeCategories.SMITHING, effectStack, data.weaponEffect().getId());
                    registry.addRecipe(emiSwordSmithingRecipeBase);
                    registry.addRecipe(new WeaponSmithingEmiRecipe(weaponRecipe, VanillaEmiRecipeCategories.SMITHING, emiSwordSmithingRecipeBase.getOutputs().stream().findAny().get().getItemStack(), effectStack, data.weaponEffect().getId() + "/second"));
                }
            }
            if(recipe instanceof SmeltingRecipe) {
                registry.addRecipe(new DiamondFurnaceEmiRecipe((AbstractCookingRecipe) recipe, DIAMOND_FURNACE_CATEGORY, 2, false));
            }
        }

        TitanFabric.LOGGER.info("Done registering TitanReborn custom EMI recipes");
    }
}

class DiamondFurnaceEmiRecipe implements EmiRecipe {
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

        if(isBetterSmeltingItem) {
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

class WeaponSmithingEmiRecipe implements EmiRecipe {
    private final WeaponRecipe recipe;
    private final EmiRecipeCategory category;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public WeaponSmithingEmiRecipe(WeaponRecipe recipe, EmiRecipeCategory category, ItemStack baseStack, ItemStack baseEssence, String additionalPath) {
        this.recipe = recipe;
        this.category = category;
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + additionalPath);

        this.inputs = List.of(
                EmiStack.of(baseStack),
                EmiStack.of(baseEssence)
        );

        this.outputs = List.of(EmiStack.of(recipe.getFakedOutput(baseStack, baseEssence)));
    }

    public WeaponSmithingEmiRecipe(WeaponRecipe recipe, EmiRecipeCategory category, ItemStack baseEssence, String additionalPath) {
        this.recipe = recipe;
        this.category = category;
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + additionalPath);

        this.inputs = List.of(
                EmiIngredient.of(recipe.base),
                EmiStack.of(baseEssence)
        );

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

class EffectEmiRecipe implements EmiRecipe {
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

