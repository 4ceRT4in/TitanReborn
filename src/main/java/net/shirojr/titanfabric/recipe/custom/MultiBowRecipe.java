package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.MultiBowHelper;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;

import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.*;

public class MultiBowRecipe extends SmithingRecipe {
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;

    public MultiBowRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, base, addition, result);
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack baseStack = inventory.getStack(0);
        ItemStack additionStack = inventory.getStack(1);
        if (!(baseStack.getItem() instanceof MultiBowItem baseCraftingItem)) return false;
        if (!(additionStack.getItem() instanceof MultiBowItem additionCraftingItem)) return false;
        if (!(result.getItem() instanceof MultiBowItem)) return false;

        int baseItemLevel = baseCraftingItem.getFullArrowCount();
        int additionItemLevel = additionCraftingItem.getFullArrowCount();
        if (baseItemLevel != additionItemLevel) return false;
        if (baseItemLevel < 1 || baseItemLevel > 2) return false;
        return this.base.test(baseStack) && this.addition.test(additionStack);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        ItemStack baseStack = inventory.getStack(0);
        ItemStack additionStack = inventory.getStack(1);
        ItemStack outputStack = this.result.copy();

        if (!(baseStack.getItem() instanceof MultiBowItem baseStackArrowSystem)) return null;
        if (!(additionStack.getItem() instanceof MultiBowItem)) return null;

        int damage = Math.max(baseStack.getDamage() - additionStack.getDamage(), additionStack.getDamage() - baseStack.getDamage());
        damage = Math.max(0, damage);
        outputStack.setDamage(damage);
        if (baseStack.hasCustomName()) outputStack.setCustomName(baseStack.getName());
        if (baseStack.hasEnchantments()) {
            var enchantmentList = EnchantmentHelper.fromNbt(baseStack.getEnchantments()).entrySet();
            for (var enchantment : enchantmentList) {
                outputStack.addEnchantment(enchantment.getKey(), enchantment.getValue());
            }
        }
        outputStack.setRepairCost(baseStack.getRepairCost());
        MultiBowHelper.setFullArrowCount(outputStack, baseStackArrowSystem.getFullArrowCount() + 1);
        return outputStack;
    }

    @Override
    public ItemStack getOutput() {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipes.MULTI_BOW_RECIPE_SERIALIZER;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(TitanFabricItems.MULTI_BOW_3);
    }

    public static class Serializer implements RecipeSerializer<MultiBowRecipe> {
        @Override
        public MultiBowRecipe read(Identifier id, JsonObject json) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
            ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            return new MultiBowRecipe(id, base, addition, result);
        }

        @Override
        public MultiBowRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient base = Ingredient.fromPacket(buf);
            Ingredient addition = Ingredient.fromPacket(buf);
            ItemStack resultStack = buf.readItemStack();
            return new MultiBowRecipe(id, base, addition, resultStack);
        }

        @Override
        public void write(PacketByteBuf buf, MultiBowRecipe recipe) {
            recipe.base.write(buf);
            recipe.addition.write(buf);
            buf.writeItemStack(recipe.result);
        }
    }
}
