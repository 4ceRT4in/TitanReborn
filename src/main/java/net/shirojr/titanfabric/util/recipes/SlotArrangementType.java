package net.shirojr.titanfabric.util.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
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

    public boolean slotsHaveMatchingItems(CraftingInventory inventory) {
        return listHasMatchingItems(effectModifierSlotIndexList, inventory) && listHasMatchingItems(baseSlotIndexList, inventory);
    }

    private static boolean listHasMatchingItems(List<Integer> list, CraftingInventory inventory) {
        ItemStack definingItem = null;
        if (list.size() < 2) return true;
        for (int validSlot : list) {
            ItemStack entry = inventory.getStack(validSlot).copy();
            entry.setCount(1);
            if (definingItem == null) {
                definingItem = entry;
                continue;
            }
            if (!definingItem.equals(entry)) return false;
            definingItem = null;
        }
        return true;
    }

    @Nullable
    public WeaponEffects getEffect(Inventory inventory) {
        ItemStack firstEffectStack = null;
        for (int i = 0; i < inventory.size(); i++) {
            if (effectModifierSlotIndexList.contains(i)) {
                firstEffectStack = inventory.getStack(i);
                break;
            }
        }
        if (firstEffectStack == null) return null;
        return WeaponEffects.getEffect(firstEffectStack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
    }
}
