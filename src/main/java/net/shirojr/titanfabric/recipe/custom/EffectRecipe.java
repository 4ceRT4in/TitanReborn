package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
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
    public final IngredientModule base;
    public final IngredientModule effectModifier;
    public final OutputModule output;
    public final SlotArrangementType slotArrangement;
    public WeaponEffectData weaponEffectData;

    public EffectRecipe(Identifier id, IngredientModule base, IngredientModule effectModifier, OutputModule output,
                        SlotArrangementType slotArrangementType) {
        super(id);
        this.base = base;
        this.effectModifier = effectModifier;
        this.output = output;
        this.slotArrangement = slotArrangementType;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int width = inventory.getWidth(), height = inventory.getHeight();
        if (width != 3 || height != 3) return false;
        WeaponEffect weaponEffect = this.slotArrangement.getEffect(inventory, this.effectModifier);

        boolean itemsMatch = this.slotArrangement.slotsHaveMatchingItems(inventory, this.base, this.effectModifier);
        itemsMatch = itemsMatch && unusedSlotsAreEmpty(this.base.slots(), this.effectModifier.slots(), inventory);
        itemsMatch = itemsMatch && this.output.ingredient.test(this.slotArrangement.getOutputItem().getDefaultStack());
        itemsMatch = itemsMatch && this.slotArrangement.supportsEffect(weaponEffect);

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
        stack.setCount(this.output.count());
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

    public ItemStack getFakedOutput(WeaponEffect weaponEffect) {
        ItemStack output = new ItemStack(this.slotArrangement.getOutputItem());
        output.setCount(this.output.count());
        WeaponEffectData effectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, 0);
        this.weaponEffectData = effectData;
        return EffectHelper.applyEffectToStack(output, this.weaponEffectData);
    }

    @Override
    public ItemStack getOutput() {
        ItemStack output = new ItemStack(this.slotArrangement.getOutputItem());
        output.setCount(this.output.count());
        return EffectHelper.applyEffectToStack(output, this.weaponEffectData);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return switch (this.slotArrangement) {
            case ARROW -> Serializer.ARROW_EFFECT_INSTANCE;
            case ESSENCE -> EffectRecipe.Serializer.ESSENCE_EFFECT_INSTANCE;
        };
    }

    public static class Serializer implements RecipeSerializer<EffectRecipe> {
        public static final EffectRecipe.Serializer ARROW_EFFECT_INSTANCE = new EffectRecipe.Serializer(SlotArrangementType.ARROW);
        public static final EffectRecipe.Serializer ESSENCE_EFFECT_INSTANCE = new EffectRecipe.Serializer(SlotArrangementType.ESSENCE);

        private final SlotArrangementType slotArrangementType;

        public Serializer(SlotArrangementType slotArrangementType) {
            this.slotArrangementType = slotArrangementType;
        }

        @Override
        public EffectRecipe read(Identifier readId, JsonObject json) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            IngredientModule baseModule = new IngredientModule(base,
                    IngredientModule.slotsFromJsonObject(json, "base"));

            Ingredient effectModifier = Ingredient.fromJson(JsonHelper.getObject(json, "modifier"));
            IngredientModule effectModifierModule = new IngredientModule(effectModifier,
                    IngredientModule.slotsFromJsonObject(json, "modifier"));

            Ingredient output = Ingredient.fromJson(JsonHelper.getObject(json, "output"));
            OutputModule outputModule = new OutputModule(output,
                    OutputModule.countFromJsonObject(json, "output"));

            return new EffectRecipe(readId, baseModule, effectModifierModule, outputModule, this.slotArrangementType);
        }

        @Override
        public EffectRecipe read(Identifier readId, PacketByteBuf buf) {
            IngredientModule effectModifier = IngredientModule.readFromPacket(buf);
            IngredientModule base = IngredientModule.readFromPacket(buf);
            OutputModule output = OutputModule.readFromPacket(buf);

            return new EffectRecipe(readId, base, effectModifier, output, this.slotArrangementType);
        }

        @Override
        public void write(PacketByteBuf buf, EffectRecipe recipe) {
            recipe.effectModifier.ingredient.write(buf);
            buf.writeIntArray(recipe.effectModifier.slots);
            recipe.base.ingredient.write(buf);
            buf.writeIntArray(recipe.base.slots);
            recipe.output.ingredient.write(buf);
            buf.writeVarInt(recipe.output.count);
        }
    }

    public static class Type implements RecipeType<EffectRecipe> {
        public static final EffectRecipe.Type ARROW_EFFECT_INSTANCE = new EffectRecipe.Type(SlotArrangementType.ARROW);
        public static final EffectRecipe.Type ESSENCE_EFFECT_INSTANCE = new EffectRecipe.Type(SlotArrangementType.ESSENCE);
        public static final String ID = "effect_recipe";

        private final SlotArrangementType slotArrangementType;

        private Type(SlotArrangementType slotArrangementType) {
            this.slotArrangementType = slotArrangementType;
        }


    }

    /**
     * IngredientModules specify one specific Ingredient and all slots, in which specifically such an
     * Ingredient needs to be present to get a successful recipe output.
     *
     * @param ingredient item, item tag, ...
     * @param slots      index of all necessary slots
     */
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

    public record OutputModule(Ingredient ingredient, int count) {
        public static OutputModule readFromPacket(PacketByteBuf buf) {
            Ingredient packetIngredient = Ingredient.fromPacket(buf);
            int count = buf.readVarInt();
            return new OutputModule(packetIngredient, count);
        }

        public static int countFromJsonObject(JsonObject json, String parentObjectKey) {
            JsonObject indexObject = JsonHelper.getObject(json, parentObjectKey);
            return JsonHelper.getInt(indexObject, "count");
        }
    }
}
