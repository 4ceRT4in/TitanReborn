package net.shirojr.titanfabric.recipe.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;

/** Repairs a used Enchanted Diamond Apple at the smithing table. */
public class EnchantedDiamondAppleRepairRecipe implements SmithingRecipe {
    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return input.template().isEmpty()
                && isDamagedApple(input.base())
                && isValidAddition(input.base(), input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        if (!isDamagedApple(input.base()) || !isValidAddition(input.base(), input.addition())) {
            return ItemStack.EMPTY;
        }

        ItemStack repaired = input.base().copy();
        repaired.setCount(1);
        if (input.addition().isOf(Items.DIAMOND_BLOCK)) {
            repaired.setDamage(repaired.getDamage() - 1);
        } else {
            repaired.setDamage(0);
        }
        return repaired;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return TitanFabricItems.ENCHANTED_DIAMOND_APPLE.getDefaultStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.ENCHANTED_DIAMOND_APPLE_REPAIR;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return stack.isEmpty();
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return isDamagedApple(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return isDamagedApple(stack) || stack.isOf(Items.DIAMOND_BLOCK);
    }

    private static boolean isDamagedApple(ItemStack stack) {
        return stack.getCount() == 1
                && stack.isOf(TitanFabricItems.ENCHANTED_DIAMOND_APPLE)
                && stack.isDamaged();
    }

    private static boolean isValidAddition(ItemStack base, ItemStack addition) {
        return addition.isOf(Items.DIAMOND_BLOCK)
                || isDamagedApple(addition) && componentsMatchExceptDamage(base, addition);
    }

    private static boolean componentsMatchExceptDamage(ItemStack first, ItemStack second) {
        ItemStack normalizedFirst = first.copy();
        ItemStack normalizedSecond = second.copy();
        normalizedFirst.setDamage(0);
        normalizedSecond.setDamage(0);
        return ItemStack.areItemsAndComponentsEqual(normalizedFirst, normalizedSecond);
    }

    public static class Serializer implements RecipeSerializer<EnchantedDiamondAppleRepairRecipe> {
        private static final EnchantedDiamondAppleRepairRecipe INSTANCE = new EnchantedDiamondAppleRepairRecipe();
        private static final MapCodec<EnchantedDiamondAppleRepairRecipe> CODEC = MapCodec.unit(INSTANCE);
        private static final PacketCodec<RegistryByteBuf, EnchantedDiamondAppleRepairRecipe> PACKET_CODEC =
                PacketCodec.unit(INSTANCE);

        @Override
        public MapCodec<EnchantedDiamondAppleRepairRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, EnchantedDiamondAppleRepairRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
