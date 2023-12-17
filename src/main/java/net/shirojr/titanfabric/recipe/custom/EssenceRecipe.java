package net.shirojr.titanfabric.recipe.custom;

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
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;

public class EssenceRecipe extends SpecialCraftingRecipe {
    private final Ingredient effectModifier;
    private final Ingredient base;
    private WeaponEffects weaponEffect;

    public EssenceRecipe(Identifier id, Ingredient effectModifier, Ingredient base) {
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
        if (itemsMatch) this.weaponEffect = slotArrangement.getEffect(inventory);
        return itemsMatch;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        this.weaponEffect = SlotArrangementType.ESSENCE.getEffect(inventory);
        if (weaponEffect == null) {
            LoggerUtil.devLogger("Couldn't find WeaponEffect from Inventory", true, null);
            return null;
        }
        ItemStack stack = new ItemStack(SlotArrangementType.ESSENCE.getOutputItem());
        return EffectHelper.getStackWithEffect(stack, weaponEffect);
    }

    @Override
    public ItemStack getOutput() {
        return EffectHelper.getStackWithEffect(new ItemStack(SlotArrangementType.ESSENCE.getOutputItem()), weaponEffect);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipes.WEAPON_EFFECT_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<EssenceRecipe> {
        @Override
        public EssenceRecipe read(Identifier id, JsonObject json) {
            Ingredient effectModifier = Ingredient.fromJson(JsonHelper.getObject(json, "modifier"));
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            return new EssenceRecipe(id, effectModifier, base);
        }

        @Override
        public EssenceRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient effectModifier = Ingredient.fromPacket(buf);
            Ingredient base = Ingredient.fromPacket(buf);
            return new EssenceRecipe(id, effectModifier, base);
        }

        @Override
        public void write(PacketByteBuf buf, EssenceRecipe recipe) {
            recipe.effectModifier.write(buf);
            recipe.base.write(buf);
        }
    }
}
