package net.shirojr.titanfabric.util.effects;

import net.minecraft.item.ItemStack;

public class ArmorPlatingHelper {

    public static boolean hasArmorPlating(ItemStack stack) {
        if (!stack.hasNbt()) return false;
        assert stack.getNbt() != null;
        return stack.getNbt().contains(ArmorPlateType.CITRIN.getId()) || stack.getNbt().contains(ArmorPlateType.DIAMOND.getId()) || stack.getNbt().contains(ArmorPlateType.EMBER.getId())
                || stack.getNbt().contains(ArmorPlateType.NETHERITE.getId()) || stack.getNbt().contains(ArmorPlateType.LEGEND.getId());
    }


    public static boolean hasArmorSpecificPlating(ItemStack stack, ArmorPlateType armorPlateType) {
        if (!stack.hasNbt()) return false;
        assert stack.getNbt() != null;
        return stack.getNbt().contains(armorPlateType.getId());
    }

    public static void removeAllArmorPlates(ItemStack stack) {
        if (!stack.hasNbt()) return;
        assert stack.getNbt() != null;
        for (ArmorPlateType type : ArmorPlateType.values()) {
            if(stack.getNbt().contains(type.getId())) {
                stack.getNbt().remove(type.getId());
            }
        }
    }

    public static void applyArmorPlate(ItemStack stack, ArmorPlateType armorPlateType) {
        if(hasArmorPlating(stack)) return;
        stack.getOrCreateNbt().putBoolean(armorPlateType.getId(), true);
    }

    public static ArmorPlateType getArmorPlatingType(ItemStack stack) {
        for (ArmorPlateType type : ArmorPlateType.values()) {
            if (hasArmorSpecificPlating(stack, type)) {
                return type;
            }
        }
        return null;
    }

    /*


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

     */

}
