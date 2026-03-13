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
import net.shirojr.titanfabric.util.effects.EffectHelper;

import java.util.stream.Stream;

public class WeaponEffectResetRecipe implements SmithingRecipe {
    final Ingredient base;

    public WeaponEffectResetRecipe(Ingredient base) {
        this.base = base;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return input.template().isEmpty()
                && input.addition().isEmpty()
                && testBase(input.base())
                && EffectHelper.hasAdditionalWeaponEffects(input.base());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        if (!input.template().isEmpty() || !input.addition().isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack inputStack = input.base();
        if (!testBase(inputStack) || !EffectHelper.hasAdditionalWeaponEffects(inputStack)) {
            return ItemStack.EMPTY;
        }

        ItemStack outputStack = inputStack.copyWithCount(1);
        EffectHelper.removeAdditionalEffectsFromStack(outputStack);
        return outputStack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.WEAPON_EFFECT_RESET;
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
        return stack.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        return Stream.of(this.base).anyMatch(Ingredient::isEmpty);
    }

    public static class Serializer implements RecipeSerializer<WeaponEffectResetRecipe> {
        private static final MapCodec<WeaponEffectResetRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base)
                        )
                        .apply(instance, WeaponEffectResetRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, WeaponEffectResetRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                WeaponEffectResetRecipe.Serializer::write, WeaponEffectResetRecipe.Serializer::read
        );

        @Override
        public MapCodec<WeaponEffectResetRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, WeaponEffectResetRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static WeaponEffectResetRecipe read(RegistryByteBuf buf) {
            Ingredient base = Ingredient.PACKET_CODEC.decode(buf);
            return new WeaponEffectResetRecipe(base);
        }

        private static void write(RegistryByteBuf buf, WeaponEffectResetRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
        }
    }
}
