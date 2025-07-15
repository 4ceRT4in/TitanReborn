package net.shirojr.titanfabric.util.effects;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;

public class ArmorPlatingHelper {

    public static boolean hasArmorPlating(ItemStack stack) {
        return stack.contains(TitanFabricDataComponents.ARMOR_PLATING);
    }

    public static boolean hasArmorSpecificPlating(ItemStack stack, ArmorPlateType armorPlateType) {
        ArmorPlateType currentType = stack.get(TitanFabricDataComponents.ARMOR_PLATING);
        return currentType != null && currentType == armorPlateType;
    }

    public static void removeAllArmorPlates(ItemStack stack) {
        stack.remove(TitanFabricDataComponents.ARMOR_PLATING);
    }

    public static void applyArmorPlate(ItemStack stack, ArmorPlateType armorPlateType) {
        if (hasArmorPlating(stack)) return;
        stack.set(TitanFabricDataComponents.ARMOR_PLATING, armorPlateType);
    }

    public static ArmorPlateType getArmorPlatingType(ItemStack stack) {
        return stack.get(TitanFabricDataComponents.ARMOR_PLATING);
    }

    public static void setArmorPlate(ItemStack stack, ArmorPlateType armorPlateType) {
        stack.set(TitanFabricDataComponents.ARMOR_PLATING, armorPlateType);
    }
}