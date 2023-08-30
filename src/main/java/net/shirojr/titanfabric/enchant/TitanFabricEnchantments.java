package net.shirojr.titanfabric.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.enchant.custom.AfterShotEnchantment;

public class TitanFabricEnchantments {

    @Deprecated
    public static Enchantment AFTER_SHOT = register("after_shot",
            new AfterShotEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.BOW, EquipmentSlot.MAINHAND));

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, new Identifier(TitanFabric.MODID, name), enchantment);
    }

    public static void registerModEnchantments() {
    }
}
