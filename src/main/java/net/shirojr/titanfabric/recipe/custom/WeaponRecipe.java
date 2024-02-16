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
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY;

public class WeaponRecipe extends SmithingRecipe {
    private final Ingredient base;
    private final Ingredient addition;
    private WeaponEffectData weaponEffectData;
    private final ItemStack result;
    public WeaponRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, base, addition, result);
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        boolean baseMatches = this.base.test(inventory.getStack(0));
        boolean additionMatches = this.addition.test(inventory.getStack(1));
        if (baseMatches && additionMatches) {
/*            WeaponEffect weaponEffect = ;
            WeaponEffectType weaponEffectType = ;
            this.weaponEffectData = new WeaponEffectData();*/
        }

        return baseMatches & additionMatches;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        ItemStack additionStack = inventory.getStack(1);
        NbtCompound compound = additionStack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);

        Optional<WeaponEffectData> weaponEffectData = WeaponEffectData.fromNbt(compound, WeaponEffectType.INNATE_EFFECT);
        if (weaponEffectData.isEmpty()) {
            LoggerUtil.devLogger("Couldn't find WeaponEffect from Inventory", true, null);
            return null;
        }
        else {
            this.weaponEffectData = weaponEffectData.get();
        }
        return null;
    }

    @Override
    public ItemStack getOutput() {
        return EffectHelper.applyEffectToStack(this.result, weaponEffectData); // FIXME: WeaponEffect is null when game start?
    }

    // public WeaponEffect getTranslatedEffect(ItemStack baseStack, ItemStack additionStack) {
        // no matching addition effect = new 25 %
        // matching

    // }

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
            return new WeaponRecipe(id, addition, base, result);
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
