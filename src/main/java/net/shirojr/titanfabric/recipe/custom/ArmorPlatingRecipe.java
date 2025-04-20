package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;

import java.util.stream.Stream;

public class ArmorPlatingRecipe extends SmithingRecipe {
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;

    public ArmorPlatingRecipe(Identifier id, Ingredient armorItem, Ingredient plateItem, ItemStack result) {
        super(id, armorItem, plateItem, result);
        this.base = armorItem;
        this.addition = plateItem;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.base.test(inventory.getStack(0)) && this.addition.test(inventory.getStack(1));
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        ItemStack inputStack = inventory.getStack(0);
        ItemStack plateStack = inventory.getStack(1);
        ItemStack outputStack = inputStack.copy();

        ArmorPlateType plateType = null;
        int i = 1;
        if (plateStack.isOf(TitanFabricItems.LEGEND_ARMOR_PLATING)) {
            plateType = ArmorPlateType.LEGEND;
            i = 500;
        } else if (plateStack.isOf(TitanFabricItems.CITRIN_ARMOR_PLATING)) {
            plateType = ArmorPlateType.CITRIN;
            i = 150;
        } else if (plateStack.isOf(TitanFabricItems.EMBER_ARMOR_PLATING)) {
            plateType = ArmorPlateType.EMBER;
            i = 300;
        } else if (plateStack.isOf(TitanFabricItems.DIAMOND_ARMOR_PLATING)) {
            plateType = ArmorPlateType.DIAMOND;
            i = 300;
        } else if (plateStack.isOf(TitanFabricItems.NETHERITE_ARMOR_PLATING)) {
            plateType = ArmorPlateType.NETHERITE;
            i = 450;
        }
        if(ArmorPlatingHelper.hasArmorPlating(outputStack)) {
            outputStack = ItemStack.EMPTY;
        }
        if (plateType != null) {
            ArmorPlatingHelper.applyArmorPlate(outputStack, plateType, i);
        }

        return outputStack;
    }

    @Override
    public ItemStack getOutput() {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(TitanFabricItems.LEGEND_ARMOR_PLATING);
    }

    public static class Serializer implements RecipeSerializer<ArmorPlatingRecipe> {
        public static final ArmorPlatingRecipe.Serializer INSTANCE = new ArmorPlatingRecipe.Serializer();

        @Override
        public ArmorPlatingRecipe read(Identifier id, JsonObject json) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
            ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            return new ArmorPlatingRecipe(id, base, addition, result);
        }

        @Override
        public ArmorPlatingRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient base = Ingredient.fromPacket(buf);
            Ingredient addition = Ingredient.fromPacket(buf);
            ItemStack resultStack = buf.readItemStack();
            return new ArmorPlatingRecipe(id, base, addition, resultStack);
        }

        @Override
        public void write(PacketByteBuf buf, ArmorPlatingRecipe recipe) {
            recipe.base.write(buf);
            recipe.addition.write(buf);
            buf.writeItemStack(recipe.result);
        }
    }

    public static class Type implements RecipeType<ArmorPlatingRecipe> {
        private Type() {
        }

        public static final ArmorPlatingRecipe.Type INSTANCE = new ArmorPlatingRecipe.Type();
        public static final String ID = "armor_plating";
    }
}