package net.shirojr.titanfabric.util.effects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.titanfabric.item.TitanFabricItems;

import java.util.Objects;

public class ArmorPlatingHelper {

    public static boolean hasArmorPlating(ItemStack stack) {
        if (!stack.hasNbt()) return false;
        assert stack.getNbt() != null;
        return stack.getNbt().contains(ArmorPlateType.CITRIN.getKey()) || stack.getNbt().contains(ArmorPlateType.DIAMOND.getKey()) || stack.getNbt().contains(ArmorPlateType.EMBER.getKey())
                || stack.getNbt().contains(ArmorPlateType.NETHERITE.getKey()) || stack.getNbt().contains(ArmorPlateType.LEGEND.getKey());
    }


    public static boolean hasArmorSpecificPlating(ItemStack stack, ArmorPlateType armorPlateType) {
        if (!stack.hasNbt()) return false;
        assert stack.getNbt() != null;
        return stack.getNbt().contains(armorPlateType.getKey());
    }


    public static void applyArmorPlate(ItemStack stack, ArmorPlateType armorPlateType, int durability) {
        if(hasArmorPlating(stack)) return;
        stack.getOrCreateNbt().putInt(armorPlateType.getKey(), durability);
    }

    public static void damage(ItemStack stack, int damage) {
        if(!hasArmorPlating(stack)) return;
        if(!stack.hasNbt()) return;
        assert stack.getNbt() != null;
        String key = Objects.requireNonNull(getArmorPlate(stack)).getKey();
        int i = stack.getNbt().getInt(key) - damage;
        if(i > 0) {
            stack.getNbt().putInt(key, i);
        } else {
            stack.getNbt().remove(key);
        }
    }

    public static int getDurability(ItemStack stack) {
        if(!hasArmorPlating(stack)) return 0;
        if(!stack.hasNbt()) return 0;
        assert stack.getNbt() != null;
        String key = Objects.requireNonNull(getArmorPlate(stack)).getKey();
        return stack.getNbt().getInt(key);
    }

    public static ArmorPlateType getArmorPlate(ItemStack stack) {
        if (!stack.hasNbt()) return null;
        NbtCompound nbt = stack.getNbt();

        for (ArmorPlateType type : ArmorPlateType.values()) {
            assert nbt != null;
            if (nbt.contains(type.getKey())) {
                return type;
            }
        }

        return null;
    }

    public static Item getPlateItem(ArmorPlateType plateType) {
        return switch (plateType) {
            case CITRIN -> TitanFabricItems.CITRIN_ARMOR_PLATING;
            case DIAMOND -> TitanFabricItems.DIAMOND_ARMOR_PLATING;
            case EMBER -> TitanFabricItems.EMBER_ARMOR_PLATING;
            case NETHERITE -> TitanFabricItems.NETHERITE_ARMOR_PLATING;
            case LEGEND -> TitanFabricItems.LEGEND_ARMOR_PLATING;
        };
    }
}
