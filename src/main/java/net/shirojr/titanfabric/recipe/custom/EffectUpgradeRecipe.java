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
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.stream.Stream;

public class EffectUpgradeRecipe implements SmithingRecipe {
    private final Ingredient base;
    private final Ingredient modifier;
    private ItemStack result;

    public EffectUpgradeRecipe(Ingredient base, Ingredient modifier) {
        this.base = base;
        this.modifier = modifier;
        this.result = ItemStack.EMPTY;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        boolean matches = input.template().isEmpty() && testBase(input.base()) && testAddition(input.addition());
        this.result = matches ? input.base().copy() : ItemStack.EMPTY;
        return matches;
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        if (!ItemStack.areEqual(this.result, input.base())) {
            this.result = input.base().copy();
        }
        if (!testBase(input.base()) || !testAddition(input.addition())) return ItemStack.EMPTY;
        WeaponEffectData modifierInnateEffect = getEffectData(input.addition(), WeaponEffectType.INNATE_EFFECT);
        WeaponEffectData baseAdditionalEffect = getEffectData(input.base(), WeaponEffectType.ADDITIONAL_EFFECT);
        if (modifierInnateEffect == null) return ItemStack.EMPTY;

        boolean invalidProduct = false;
        HashSet<WeaponEffectData> outputWeaponEffects = new HashSet<>(this.result.getOrDefault(TitanFabricDataComponents.WEAPON_EFFECTS, new HashSet<>()));
        if (baseAdditionalEffect == null) {
            outputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, modifierInnateEffect.weaponEffect(), modifierInnateEffect.strength()));
        } else {
            outputWeaponEffects.removeIf(effectData -> effectData.type().equals(WeaponEffectType.ADDITIONAL_EFFECT) &&
                    effectData.weaponEffect().equals(modifierInnateEffect.weaponEffect()));
            int combinedStrength = baseAdditionalEffect.strength() + modifierInnateEffect.strength();
            if (combinedStrength > WeaponEffect.MAX_LEVEL) {
                invalidProduct = true;
            }
            outputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, modifierInnateEffect.weaponEffect(), combinedStrength));
            if (outputWeaponEffects.stream().filter(effectData -> effectData.type().equals(WeaponEffectType.ADDITIONAL_EFFECT)).count() > 1) {
                invalidProduct = true;
            }
        }
        if (invalidProduct) {
            this.result = ItemStack.EMPTY;
            return this.result;
        }

        this.result.set(TitanFabricDataComponents.WEAPON_EFFECTS, outputWeaponEffects);
        return this.result;
    }

    @Nullable
    private static WeaponEffectData getEffectData(ItemStack stack, WeaponEffectType type) {
        HashSet<WeaponEffectData> weaponEffects = stack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (weaponEffects == null) return null;
        WeaponEffectData innateEffect = null;
        for (WeaponEffectData entry : weaponEffects) {
            if (entry.type().equals(type)) {
                innateEffect = entry;
                break;
            }
        }
        return innateEffect;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.EFFECT_UPGRADE;
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
        return this.modifier.test(stack);
    }

    @Override
    public boolean isEmpty() {
        return Stream.of(this.base, this.modifier).anyMatch(Ingredient::isEmpty);
    }

    public static class Serializer implements RecipeSerializer<EffectUpgradeRecipe> {
        private static final MapCodec<EffectUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.modifier)
                        )
                        .apply(instance, EffectUpgradeRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, EffectUpgradeRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                EffectUpgradeRecipe.Serializer::write, EffectUpgradeRecipe.Serializer::read
        );

        @Override
        public MapCodec<EffectUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, EffectUpgradeRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static EffectUpgradeRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
            return new EffectUpgradeRecipe(ingredient2, ingredient3);
        }

        private static void write(RegistryByteBuf buf, EffectUpgradeRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.modifier);
        }
    }
}
