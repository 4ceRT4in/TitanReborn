package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;

import java.util.stream.IntStream;

public class EffectRecipe extends SpecialCraftingRecipe {
    private final IngredientModule effectModifier;
    private final IngredientModule base;
    private final SlotArrangementType slotArrangement;
    private WeaponEffectData weaponEffectData;

    public EffectRecipe(Identifier id, IngredientModule effectModifier, IngredientModule base, SlotArrangementType slotArrangementType) {
        super(id);
        this.effectModifier = effectModifier;
        this.base = base;
        this.slotArrangement = slotArrangementType;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int width = inventory.getWidth(), height = inventory.getHeight();
        if (width != 3 || height != 3) return false;
        boolean itemsMatch = this.slotArrangement.slotsHaveMatchingItems(inventory, this.base, this.effectModifier);
        itemsMatch = itemsMatch && unusedSlotsAreEmpty(this.base.slots(), this.effectModifier.slots(), inventory);
        WeaponEffect weaponEffect = slotArrangement.getEffect(inventory, this.effectModifier);

        if (itemsMatch && weaponEffect != null) {
            this.weaponEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, 0);
        }
        return itemsMatch;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        WeaponEffect weaponEffect = this.slotArrangement.getEffect(inventory, this.effectModifier);
        if (weaponEffect == null) {
            LoggerUtil.devLogger("Couldn't find WeaponEffect from Inventory", true, null);
            return null;
        }
        WeaponEffectData effectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, 0);
        this.weaponEffectData = effectData;
        ItemStack stack = new ItemStack(this.slotArrangement.getOutputItem());
        return EffectHelper.applyEffectToStack(stack, effectData);
    }

    private static boolean unusedSlotsAreEmpty(int[] baseSlots, int[] effectModifierSlots, CraftingInventory inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            int currentSlot = i;
            if (IntStream.of(baseSlots).anyMatch(value -> value == currentSlot)) continue;
            if (IntStream.of(effectModifierSlots).anyMatch(value -> value == currentSlot)) continue;
            if (!inventory.getStack(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return EffectHelper.applyEffectToStack(new ItemStack(this.slotArrangement.getOutputItem()), weaponEffectData);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.slotArrangement.getSerializer();
    }

    public static class Serializer implements RecipeSerializer<EffectRecipe> {
        private final SlotArrangementType slotArrangementType;

        public Serializer(SlotArrangementType slotArrangementType) {
            this.slotArrangementType = slotArrangementType;
        }

        @Override
        public EffectRecipe read(Identifier id, JsonObject json) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            IngredientModule baseModule = new IngredientModule(base,
                    IngredientModule.slotsFromJsonObject(json, "base"));

            Ingredient effectModifier = Ingredient.fromJson(JsonHelper.getObject(json, "modifier"));
            IngredientModule effectModifierModule = new IngredientModule(effectModifier,
                    IngredientModule.slotsFromJsonObject(json, "modifier"));

            return new EffectRecipe(id, effectModifierModule, baseModule, this.slotArrangementType);
        }

        @Override
        public EffectRecipe read(Identifier id, PacketByteBuf buf) {
            IngredientModule effectModifier = IngredientModule.readFromPacket(buf);
            IngredientModule base = IngredientModule.readFromPacket(buf);

            return new EffectRecipe(id, effectModifier, base, this.slotArrangementType);
        }

        @Override
        public void write(PacketByteBuf buf, EffectRecipe recipe) {
            recipe.effectModifier.ingredient.write(buf);
            buf.writeIntArray(recipe.effectModifier.slots);
            recipe.base.ingredient.write(buf);
            buf.writeIntArray(recipe.base.slots);
        }
    }

    public record IngredientModule(Ingredient ingredient, int[] slots) {
        public static IngredientModule readFromPacket(PacketByteBuf buf) {
            Ingredient packetIngredient = Ingredient.fromPacket(buf);
            int[] packetSlots = buf.readIntArray();
            return new IngredientModule(packetIngredient, packetSlots);
        }

        public static int[] slotsFromJsonObject(JsonObject json, String parentObjectKey) {
            JsonObject indexObject = JsonHelper.getObject(json, parentObjectKey);
            JsonArray indexArray = JsonHelper.getArray(indexObject, "slots");
            int[] slotIndexList = new int[indexArray.size()];

            for (int i = 0; i < slotIndexList.length; i++) {
                slotIndexList[i] = indexArray.get(i).getAsInt();
            }
            return slotIndexList;
        }
    }
}
