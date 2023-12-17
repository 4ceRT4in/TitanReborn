package net.shirojr.titanfabric.util.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.recipe.custom.EssenceRecipe;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.EssenceCrafting;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

public enum SlotArrangementType {
    ESSENCE(TitanFabricItems.ESSENCE),
    ARROW(TitanFabricItems.ARROW),
    CITRIN_SWORD(TitanFabricItems.CITRIN_SWORD);  // TODO: use in another recipe type?

    private final Item outputItem;

    SlotArrangementType(Item outputItem) {
        this.outputItem = outputItem;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public boolean slotsHaveMatchingItems(CraftingInventory inventory, EssenceRecipe.IngredientModule base,
                                          EssenceRecipe.IngredientModule effectModifier) {
        boolean effectModifierMatchesItems = inventoryContainsValidItems(inventory, effectModifier);
        boolean baseMatchesItems = inventoryContainsValidItems(inventory, base);

        WeaponEffects validEffect = null;
        for (var entry : WeaponEffects.values()) {
            WeaponEffects currentEffect = getEffect(inventory, effectModifier);
            if (entry.equals(currentEffect)) validEffect = currentEffect;
        }
        if (validEffect == null) return false;
        return effectModifierMatchesItems && baseMatchesItems;
    }

    private static boolean inventoryContainsValidItems(CraftingInventory inventory, EssenceRecipe.IngredientModule ingredient) {
        for (int validSlot : ingredient.slots()) {
            ItemStack entry = inventory.getStack(validSlot);
            if (!ingredient.ingredient().test(entry)) return false;
        }
        return true;
    }

    @Nullable
    public WeaponEffects getEffect(Inventory inventory, EssenceRecipe.IngredientModule effectModifierModule) {
        WeaponEffects effect;
        ItemStack firstEffectStack = null;

        for (int i = 0; i < inventory.size(); i++) {
            if (ArrayUtils.contains(effectModifierModule.slots(), i)) {
                firstEffectStack = inventory.getStack(i);
                break;
            }
        }
        if (firstEffectStack == null) return null;
        if (firstEffectStack.isOf(Items.POTION)) {
            effect = EffectHelper.getWeaponEffectFromPotion(firstEffectStack);
        } else if (firstEffectStack.getItem() instanceof EssenceCrafting essenceIngredient) {
            effect = essenceIngredient.ingredientEffect(firstEffectStack);
        } else {
            effect = WeaponEffects.getEffect(firstEffectStack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
        }

        return effect;
    }
}
