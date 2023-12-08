package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.util.LoggerUtil;

import java.util.List;

public class WeaponEffectRecipe extends SpecialCraftingRecipe {
    private final Ingredient effectModifier;
    private final Ingredient base;

    private SlotArrangementType slotArrangementType;

    public WeaponEffectRecipe(Identifier id, Ingredient effectModifier, Ingredient base) {
        super(id);
        this.effectModifier = effectModifier;
        this.base = base;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        LoggerUtil.devLogger("testing for recipe");
        int width = inventory.getWidth(), height = inventory.getHeight();
        if (width != 3 || height != 3) return false;
        for (SlotArrangementType slotArrangementType : SlotArrangementType.values()) {
            if (!slotArrangementType.slotsHaveMatchingItems(inventory)) return false;
            this.slotArrangementType = slotArrangementType;
        }


        if (!effectModifier.test(inventory.getStack(1))) return false;
        return (base.test(inventory.getStack(2)));
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        return new ItemStack(Items.SEA_PICKLE);
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.ACACIA_BOAT);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipes.WEAPON_EFFECT_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<WeaponEffectRecipe> {
        @Override
        public WeaponEffectRecipe read(Identifier id, JsonObject json) {
            Ingredient effectModifier = Ingredient.fromJson(JsonHelper.getObject(json, "effect_modifier"));
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            return new WeaponEffectRecipe(id, effectModifier, base);
        }

        @Override
        public WeaponEffectRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient effectModifier = Ingredient.fromPacket(buf);
            Ingredient base = Ingredient.fromPacket(buf);
            return new WeaponEffectRecipe(id, effectModifier, base);
        }

        @Override
        public void write(PacketByteBuf buf, WeaponEffectRecipe recipe) {
            recipe.effectModifier.write(buf);
            recipe.base.write(buf);
        }
    }

    public enum SlotArrangementType {
        //ESSENCE(TitanFabricItems.ESSENCE, List.of(1, 3, 5, 7), List.of(4)), // TODO: merge ESSENCE item into one?
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
    }
}
