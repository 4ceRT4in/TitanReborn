package net.shirojr.titanfabric.recipe.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;
import net.shirojr.titanfabric.util.IngredientModule;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EffectRecipe extends SpecialCraftingRecipe {
    private final IngredientModule base;
    private final IngredientModule modifier;
    private final Result result;
    @Nullable
    private WeaponEffectData effectData;

    public static final CraftingRecipeCategory CATEGORY = CraftingRecipeCategory.MISC;

    public EffectRecipe(IngredientModule base, IngredientModule modifier, Result result) {
        super(CraftingRecipeCategory.MISC);
        this.base = base;
        this.modifier = modifier;
        this.result = result;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        int matchingStacks = 0;
        this.effectData = null;
        for (int i = 0; i < input.getStacks().size(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (base.test(stack, i)) {
                matchingStacks++;
            } else if (modifier.test(stack, i)) {
                matchingStacks++;
                this.getWeaponEffectIfMissing(stack);
            } else if (stack.isEmpty() && !base.contains(i) && !modifier.contains(i)) {
                matchingStacks++;
            }
        }
        return matchingStacks == 9;
    }

    private void getWeaponEffectIfMissing(ItemStack modifierStack) {
        WeaponEffect weaponEffect = null;
        if (this.effectData != null) return;
        if (modifierStack.getItem() instanceof PotionItem) {
            weaponEffect = EffectHelper.getWeaponEffectFromPotion(modifierStack);
        } else if (modifierStack.getItem() instanceof WeaponEffectCrafting) {
            weaponEffect = WeaponEffectData.get(modifierStack, WeaponEffectType.INNATE_EFFECT).map(WeaponEffectData::weaponEffect).orElse(null);
        }
        if (weaponEffect == null) {
            throw new NullPointerException("Modifier Stack didn't have any WeaponEffect");
        }
        this.effectData = new WeaponEffectData(
                WeaponEffectType.INNATE_EFFECT,
                weaponEffect,
                1
        );
    }

    @Override
    public boolean fits(int width, int height) {
        return base.fits(width, height) || modifier.fits(width, height);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.getResultStack();
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.getResultStack();
    }

    private ItemStack getResultStack() {
        if (this.effectData == null) {
            TitanFabric.LOGGER.warn("No Effect Data available", new NullPointerException());
            return ItemStack.EMPTY;
        }
        ItemStack stack = EffectHelper.applyEffectToStack(this.result.item.getDefaultStack().copyWithCount(this.result.count), this.effectData, false);
        return Optional.of(stack).orElseThrow();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.EFFECT;
    }

    public static class Serializer implements RecipeSerializer<EffectRecipe> {
        public static final MapCodec<EffectRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        IngredientModule.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                        IngredientModule.CODEC.fieldOf("modifier").forGetter(recipe -> recipe.modifier),
                        Result.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                ).apply(instance, EffectRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, EffectRecipe> PACKET_CODEC = PacketCodec.tuple(
                IngredientModule.PACKET_CODEC, effectRecipe -> effectRecipe.base,
                IngredientModule.PACKET_CODEC, effectRecipe -> effectRecipe.modifier,
                Result.PACKET_CODEC, effectRecipe -> effectRecipe.result,
                EffectRecipe::new
        );

        @Override
        public MapCodec<EffectRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, EffectRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }

    public record Result(Item item, int count) {
        public static final MapCodec<Result> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.xmap(Registries.ITEM::get, Registries.ITEM::getId).fieldOf("result").forGetter(Result::item),
                Codec.INT.fieldOf("count").forGetter(Result::count)
        ).apply(instance, Result::new));

        public static final PacketCodec<RegistryByteBuf, Result> PACKET_CODEC = PacketCodec.tuple(
                Identifier.PACKET_CODEC.xmap(Registries.ITEM::get, Registries.ITEM::getId), Result::item,
                PacketCodecs.VAR_INT, Result::count,
                Result::new
        );
    }
}
