package net.shirojr.titanfabric.util.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.EssenceCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum SlotArrangementType {
    ESSENCE(TitanFabricItems.ESSENCE, List.of(1, 3, 5, 7), List.of(4)),
    ARROW(TitanFabricItems.ARROW, List.of(4), List.of(1, 3, 5, 7)),
    CITRIN_SWORD(TitanFabricItems.CITRIN_SWORD, List.of(), List.of());  // TODO: use in another recipe type?

    private final List<Integer> effectModifierSlotIndexList;
    private final List<Integer> baseSlotIndexList;

    private final Item outputItem;

    SlotArrangementType(Item outputItem, List<Integer> effectModifierSlotIndexList, List<Integer> baseSlotIndexList) {
        this.outputItem = outputItem;
        this.effectModifierSlotIndexList = effectModifierSlotIndexList;
        this.baseSlotIndexList = baseSlotIndexList;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public boolean slotsHaveMatchingItems(CraftingInventory inventory, Ingredient base, Ingredient effectModifier) {
        boolean effectModifierMatchesItems = inventoryContainsValidItems(effectModifierSlotIndexList, inventory, effectModifier);
        boolean baseMatchesItems = inventoryContainsValidItems(baseSlotIndexList, inventory, base);

        WeaponEffects validEffect = null;
        for (var entry : WeaponEffects.values()) {
            WeaponEffects currentEffect = getEffect(inventory);
            if (entry.equals(currentEffect)) validEffect = currentEffect;
        }
        if (validEffect == null) return false;
        return effectModifierMatchesItems && baseMatchesItems;
    }

    private static boolean inventoryContainsValidItems(List<Integer> list, CraftingInventory inventory, Ingredient ingredient) {
        for (int validSlot : list) {
            ItemStack entry = inventory.getStack(validSlot);
            if (!ingredient.test(entry)) return false;
        }
        return true;
    }

    @Nullable
    public WeaponEffects getEffect(Inventory inventory) {
        WeaponEffects effect;
        ItemStack firstEffectStack = null;

        for (int i = 0; i < inventory.size(); i++) {
            if (effectModifierSlotIndexList.contains(i)) {
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

    public ItemStack getEffectModifierItemStack(Inventory inventory) {
        return inventory.getStack(this.effectModifierSlotIndexList.get(0)).copy();
    }

    public ItemStack getBaseItemStack(Inventory inventory) {
        return inventory.getStack(this.baseSlotIndexList.get(0)).copy();
    }
}
