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
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.HashSet;
import java.util.stream.Stream;

public class WeaponEffectRecipe implements SmithingRecipe {
    final Ingredient base;
    final Ingredient addition;

    public WeaponEffectRecipe(Ingredient base, Ingredient addition) {
        this.base = base;
        this.addition = addition;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return input.template().isEmpty() && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack baseStack = input.base();
        if (!this.base.test(baseStack) || !this.addition.test(input.addition())) return ItemStack.EMPTY;
        HashSet<WeaponEffectData> weaponEffects = input.addition().get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (weaponEffects == null) return ItemStack.EMPTY;
        WeaponEffectData innateEffect = null;
        for (WeaponEffectData entry : weaponEffects) {
            if (entry.type().equals(WeaponEffectType.INNATE_EFFECT)) {
                innateEffect = entry;
                break;
            }
        }
        if (innateEffect == null) return ItemStack.EMPTY;
        ItemStack outputStack = baseStack.copyWithCount(1);
        HashSet<WeaponEffectData> outputWeaponEffects = baseStack.getOrDefault(TitanFabricDataComponents.WEAPON_EFFECTS, new HashSet<>());
        outputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, innateEffect.weaponEffect(), innateEffect.strength()));
        outputStack.set(TitanFabricDataComponents.WEAPON_EFFECTS, outputWeaponEffects);
        return outputStack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        ItemStack resultStack = TitanFabricItems.LEGEND_SWORD.getDefaultStack();
        WeaponEffect weaponEffect = TitanFabricItems.LEGEND_SWORD.supportedEffects().get(0);
        HashSet<WeaponEffectData> weaponEffectData = new HashSet<>();
        weaponEffectData.add(new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, weaponEffect, 2));
        resultStack.set(TitanFabricDataComponents.WEAPON_EFFECTS, weaponEffectData);
        return resultStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.WEAPON_EFFECT;
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

    public static class Serializer implements RecipeSerializer<WeaponEffectRecipe> {
        private static final MapCodec<WeaponEffectRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
                        )
                        .apply(instance, WeaponEffectRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, WeaponEffectRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                WeaponEffectRecipe.Serializer::write, WeaponEffectRecipe.Serializer::read
        );

        @Override
        public MapCodec<WeaponEffectRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, WeaponEffectRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static WeaponEffectRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
            return new WeaponEffectRecipe(ingredient2, ingredient3);
        }

        private static void write(RegistryByteBuf buf, WeaponEffectRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
        }
    }
}
