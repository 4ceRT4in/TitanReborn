package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;

import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.*;

public class WeaponRecipe extends SmithingRecipe {
    private final Ingredient base;
    private final Ingredient addition;
    private WeaponEffectData weaponEffectData;
    private ItemStack result;

    public WeaponRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, base, addition, result);
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack baseStack = inventory.getStack(0);
        ItemStack additionStack = inventory.getStack(1);
        if (!(baseStack.getItem() instanceof WeaponEffectCrafting baseCraftingItem)) return false;
        if (!baseCraftingItem.isType(WeaponEffectCrafting.ItemType.PRODUCT)) return false;
        if (!(additionStack.getItem() instanceof WeaponEffectCrafting additionCraftingItem)) return false;
        if (!additionCraftingItem.isType(WeaponEffectCrafting.ItemType.INGREDIENT)) return false;
        boolean baseMatches = this.base.test(baseStack);
        boolean additionMatches = this.addition.test(additionStack);

        if (!additionStack.getOrCreateNbt().contains(EFFECTS_COMPOUND_NBT_KEY)) return false;
        NbtCompound additionCompound = additionStack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);
        NbtCompound baseCompound = baseStack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);
        Optional<WeaponEffectData> baseAdditionData = WeaponEffectData.fromNbt(baseCompound, WeaponEffectType.ADDITIONAL_EFFECT);
        Optional<WeaponEffectData> modifierInnateData = WeaponEffectData.fromNbt(additionCompound, WeaponEffectType.INNATE_EFFECT);
        if (modifierInnateData.isEmpty()) return false;
        if (baseAdditionData.isPresent()) {
            if (baseAdditionData.get().strength() >= 2) return false;
            if (!baseAdditionData.get().weaponEffect().equals(modifierInnateData.get().weaponEffect())) return false;
        }
        NbtCompound typeCompound = additionCompound.getCompound(WeaponEffectType.INNATE_EFFECT.getNbtKey());
        WeaponEffectData effectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, WeaponEffect.getEffect(typeCompound.getString(EFFECT_NBT_KEY)), typeCompound.getInt(EFFECTS_STRENGTH_NBT_KEY));

        if (baseMatches && additionMatches) {
            EffectHelper.removeEffectsFromStack(this.result);
            this.result = getOutput();
            this.weaponEffectData = effectData;
        }
        return baseMatches && additionMatches;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        ItemStack baseStack = inventory.getStack(0);
        ItemStack additionStack = inventory.getStack(1);
        NbtCompound baseCompound = baseStack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);
        NbtCompound additionCompound = additionStack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);

        Optional<WeaponEffectData> baseInnateEffectData = WeaponEffectData.fromNbt(baseCompound, WeaponEffectType.INNATE_EFFECT);
        Optional<WeaponEffectData> baseAdditionEffectData = WeaponEffectData.fromNbt(baseCompound, WeaponEffectType.ADDITIONAL_EFFECT);
        Optional<WeaponEffectData> modifierInnateEffectData = WeaponEffectData.fromNbt(additionCompound, WeaponEffectType.INNATE_EFFECT);

        if (modifierInnateEffectData.isPresent()) {
            this.weaponEffectData = modifierInnateEffectData.get();
            ItemStack itemStack = this.result.copy();
            int strength = 1;
            if (baseAdditionEffectData.isPresent()) {
                strength = baseAdditionEffectData.get().strength() + modifierInnateEffectData.get().strength();
                strength = Math.min(2, Math.max(1, strength));
            }
            if (baseInnateEffectData.isPresent()) {
                NbtCompound finalBaseCompound = baseInnateEffectData.get().toNbt();
                itemStack.getOrCreateNbt().put(EFFECTS_COMPOUND_NBT_KEY, finalBaseCompound);
            }
            NbtCompound finalAdditionCompound = new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT,
                    modifierInnateEffectData.get().weaponEffect(), strength).toNbt();

            itemStack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY)
                    .put(WeaponEffectType.ADDITIONAL_EFFECT.getNbtKey(),
                            finalAdditionCompound.getCompound(WeaponEffectType.ADDITIONAL_EFFECT.getNbtKey()));
            this.result = itemStack.copy();
            return itemStack;
        }

        LoggerUtil.devLogger("Couldn't find WeaponEffect of modifier stack", true, null);
        return null;
    }

    @Override
    public ItemStack getOutput() {
        WeaponEffectData data = this.weaponEffectData;
        if (data == null) return this.result;
        int strength = data.strength() + 1;
        strength = Math.min(2, Math.max(1, strength));
        WeaponEffectData newData = new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, weaponEffectData.weaponEffect(), strength);
        return EffectHelper.applyEffectToStack(this.result, newData);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipes.WEAPON_EFFECT_RECIPE_SERIALIZER;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(TitanFabricItems.LEGEND_INGOT);
    }

    public static class Serializer implements RecipeSerializer<WeaponRecipe> {
        @Override
        public WeaponRecipe read(Identifier id, JsonObject json) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "modifier"));
            ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            return new WeaponRecipe(id, base, addition, result);
        }

        @Override
        public WeaponRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient base = Ingredient.fromPacket(buf);
            Ingredient addition = Ingredient.fromPacket(buf);
            ItemStack resultStack = buf.readItemStack();
            return new WeaponRecipe(id, base, addition, resultStack);
        }

        @Override
        public void write(PacketByteBuf buf, WeaponRecipe recipe) {
            recipe.base.write(buf);
            recipe.addition.write(buf);
            buf.writeItemStack(recipe.result);
        }
    }
}
