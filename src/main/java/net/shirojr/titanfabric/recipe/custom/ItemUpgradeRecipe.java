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
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;

import java.util.stream.Stream;

public class ItemUpgradeRecipe implements SmithingRecipe {
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public ItemUpgradeRecipe(Ingredient base, Ingredient addition, ItemStack result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return input.template().isEmpty() && testBase(input.base()) && testAddition(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        if (!input.template().isEmpty() || !testBase(input.base()) || !testAddition(input.addition())) {
            return ItemStack.EMPTY;
        }
        ItemStack output = input.base().copyComponentsToNewStack(this.result.getItem(), this.result.getCount());
        output.applyUnvalidatedChanges(this.result.getComponentChanges());
        return output;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.ITEM_UPGRADE;
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

    public static class Serializer implements RecipeSerializer<ItemUpgradeRecipe> {
        private static final MapCodec<ItemUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                        )
                        .apply(instance, ItemUpgradeRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, ItemUpgradeRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                ItemUpgradeRecipe.Serializer::write, ItemUpgradeRecipe.Serializer::read
        );

        @Override
        public MapCodec<ItemUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ItemUpgradeRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ItemUpgradeRecipe read(RegistryByteBuf buf) {
            Ingredient base = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient addition = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            return new ItemUpgradeRecipe(base, addition, result);
        }

        private static void write(RegistryByteBuf buf, ItemUpgradeRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        }
    }
}
