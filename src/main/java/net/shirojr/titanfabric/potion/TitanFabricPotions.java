package net.shirojr.titanfabric.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricPotions {
    public static Potion INDESTRUCTIBLE_POTION = registerPotion("indestructibility_potion", 3600, 0);
    public static Potion LONG_INDESTRUCTIBLE_POTION = registerPotion("long_indestructibility_potion", 9600, 0);

    public static Potion registerPotion(String id, int duration, int amplifier) {
        return Registry.register(Registry.POTION, new Identifier(TitanFabric.MODID, id),
                new Potion(new StatusEffectInstance(TitanFabricStatusEffects.INDESTRUCTIBILITY, duration, amplifier)));
    }

    public static void register() {
        LoggerUtil.devLogger("initializing potions");
    }
}
