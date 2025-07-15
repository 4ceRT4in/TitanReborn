package net.shirojr.titanfabric.color;

import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

import java.util.List;

public class TitanFabricDyeProviders {

    public static void applyColorTooltip(List<Text> tooltip, ItemStack stack) {
        var comp = stack.get(DataComponentTypes.BASE_COLOR);
        if (comp != null) {
            for (DyeColor dyes : DyeColor.values()) {
                if (comp.getName().equals(dyes.getName())) {
                    tooltip.add(Text.translatable("tooltip.titanfabric.color." + dyes.getName()));
                    return;
                }
            }
        }

        if (stack.getItem() instanceof TitanFabricParachuteItem) {
            tooltip.add(Text.translatable("tooltip.titanfabric.color.white"));
        }
    }

    public static String getColorFromNBT(ItemStack stack) {
        var comp = stack.get(DataComponentTypes.BASE_COLOR);
        if (comp != null) {
            for (DyeColor dyes : DyeColor.values()) {
                if (comp.getName().equals(dyes.getName())) {
                    return dyes.getName();
                }
            }
        }
        return "white";
    }
}
