package net.shirojr.titanfabric.recipe.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;

import java.util.Optional;
import java.util.stream.Stream;

public class MultiBowUpgradeRecipe implements SmithingRecipe {
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public MultiBowUpgradeRecipe(Ingredient base, Ingredient addition, ItemStack result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }


    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        if (getValidLevel(input.base(), input.addition()).isEmpty()) return false;
        return input.template().isEmpty() && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        Optional<Integer> level = getValidLevel(input.base(), input.addition());
        if (level.isEmpty()) return ItemStack.EMPTY;
        ItemStack itemStack = input.base().copyComponentsToNewStack(this.result.getItem(), this.result.getCount());
        itemStack.set(TitanFabricDataComponents.MULTI_BOW_MAX_ARROWS_COUNT, level.get() + 1);
        itemStack.applyUnvalidatedChanges(this.result.getComponentChanges());
        return itemStack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    private static Optional<Integer> getValidLevel(ItemStack base, ItemStack addition) {
        int baseLevel = base.getOrDefault(TitanFabricDataComponents.MULTI_BOW_MAX_ARROWS_COUNT, -1);
        int additionLevel = addition.getOrDefault(TitanFabricDataComponents.MULTI_BOW_MAX_ARROWS_COUNT, -1);
        if (baseLevel != additionLevel) return Optional.empty();
        if (baseLevel < 0/* || baseLevel >= MAX_ALLOWED_LEVEL*/) return Optional.empty();
        return Optional.of(baseLevel);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.MULTI_BOW_UPGRADE;
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

    public static class Serializer implements RecipeSerializer<MultiBowUpgradeRecipe> {
        private static final MapCodec<MultiBowUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                        )
                        .apply(instance, MultiBowUpgradeRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, MultiBowUpgradeRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                MultiBowUpgradeRecipe.Serializer::write, MultiBowUpgradeRecipe.Serializer::read
        );

        @Override
        public MapCodec<MultiBowUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MultiBowUpgradeRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static MultiBowUpgradeRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            return new MultiBowUpgradeRecipe(ingredient2, ingredient3, itemStack);
        }

        private static void write(RegistryByteBuf buf, MultiBowUpgradeRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        }
    }
}
