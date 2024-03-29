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
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;

public class EssenceRecipe extends SpecialCraftingRecipe {
    private final IngredientModule effectModifier;
    private final IngredientModule base;
    private WeaponEffectData weaponEffectData;

    public EssenceRecipe(Identifier id, IngredientModule effectModifier, IngredientModule base) {
        super(id);
        this.effectModifier = effectModifier;
        this.base = base;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int width = inventory.getWidth(), height = inventory.getHeight();
        if (width != 3 || height != 3) return false;
        SlotArrangementType slotArrangement = SlotArrangementType.ESSENCE;
        boolean itemsMatch = slotArrangement.slotsHaveMatchingItems(inventory, this.base, this.effectModifier);
        if (itemsMatch) {
            WeaponEffect weaponEffect = slotArrangement.getEffect(inventory, this.effectModifier);
            this.weaponEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, 0);
        }
        return itemsMatch;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        WeaponEffect weaponEffect = SlotArrangementType.ESSENCE.getEffect(inventory, this.effectModifier);
        WeaponEffectData effectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, 0);
        this.weaponEffectData = effectData;
        if (weaponEffect == null) {
            LoggerUtil.devLogger("Couldn't find WeaponEffect from Inventory", true, null);
            return null;
        }
        ItemStack stack = new ItemStack(SlotArrangementType.ESSENCE.getOutputItem());
        return EffectHelper.applyEffectToStack(stack, effectData);
    }

    @Override
    public ItemStack getOutput() {
        return EffectHelper.applyEffectToStack(new ItemStack(SlotArrangementType.ESSENCE.getOutputItem()), weaponEffectData);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipes.ESSENCE_EFFECT_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<EssenceRecipe> {
        @Override
        public EssenceRecipe read(Identifier id, JsonObject json) {
            Ingredient effectModifier = Ingredient.fromJson(JsonHelper.getObject(json, "modifier"));
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));

            IngredientModule baseModule = new IngredientModule(base,
                    IngredientModule.slotsFromJsonObject(json, "base"));
            IngredientModule effectModifierModule = new IngredientModule(effectModifier,
                    IngredientModule.slotsFromJsonObject(json, "modifier"));

            return new EssenceRecipe(id, effectModifierModule, baseModule);
        }

        @Override
        public EssenceRecipe read(Identifier id, PacketByteBuf buf) {
            IngredientModule effectModifier = IngredientModule.readFromPacket(buf);
            IngredientModule base = IngredientModule.readFromPacket(buf);

            return new EssenceRecipe(id, effectModifier, base);
        }

        @Override
        public void write(PacketByteBuf buf, EssenceRecipe recipe) {
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
