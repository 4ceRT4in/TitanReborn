package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;

import java.util.stream.Stream;

public class ArmorPlatingRecipe implements SmithingRecipe {
    final Ingredient base;
    final Ingredient addition;

    public ArmorPlatingRecipe(Ingredient base, Ingredient addition) {
        this.base = base;
        this.addition = addition;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return input.template().isEmpty() && testBase(input.base()) && testAddition(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack inputStack = input.base();
        ItemStack plateStack = input.addition();

        if (!testBase(inputStack) || !testAddition(plateStack)) {
            return ItemStack.EMPTY;
        }

        ItemStack outputStack = inputStack.copyWithCount(1);

        ArmorPlateType plateType = null;
        if (plateStack.isOf(TitanFabricItems.LEGEND_ARMOR_PLATING)) {
            plateType = ArmorPlateType.LEGEND;
        } else if (plateStack.isOf(TitanFabricItems.CITRIN_ARMOR_PLATING)) {
            plateType = ArmorPlateType.CITRIN;
        } else if (plateStack.isOf(TitanFabricItems.EMBER_ARMOR_PLATING)) {
            plateType = ArmorPlateType.EMBER;
        } else if (plateStack.isOf(TitanFabricItems.DIAMOND_ARMOR_PLATING)) {
            plateType = ArmorPlateType.DIAMOND;
        } else if (plateStack.isOf(TitanFabricItems.NETHERITE_ARMOR_PLATING)) {
            plateType = ArmorPlateType.NETHERITE;
        }

        if (!outputStack.hasEnchantments()) {
            return ItemStack.EMPTY;
        }

        if (ArmorPlatingHelper.hasArmorPlating(outputStack)) {
            ArmorPlatingHelper.removeAllArmorPlates(outputStack);
        }

        if (plateType != null) {
            ArmorPlatingHelper.applyArmorPlate(outputStack, plateType);
        }

        return outputStack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        ItemStack resultStack = new ItemStack(TitanFabricItems.LEGEND_ARMOR_PLATING);
        return resultStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.ARMOR_PLATING;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return stack.isEmpty();
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public boolean isEmpty() {
        return Stream.of(this.base, this.addition).anyMatch(Ingredient::isEmpty);
    }

    public static class Serializer implements RecipeSerializer<ArmorPlatingRecipe> {
        private static final MapCodec<ArmorPlatingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
                        )
                        .apply(instance, ArmorPlatingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, ArmorPlatingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                ArmorPlatingRecipe.Serializer::write, ArmorPlatingRecipe.Serializer::read
        );

        @Override
        public MapCodec<ArmorPlatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ArmorPlatingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ArmorPlatingRecipe read(RegistryByteBuf buf) {
            Ingredient base = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient addition = Ingredient.PACKET_CODEC.decode(buf);
            return new ArmorPlatingRecipe(base, addition);
        }

        private static void write(RegistryByteBuf buf, ArmorPlatingRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
        }
    }
}