package net.shirojr.titanfabric.effect.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;

public class TitanFabricPotions {
    public static Potion INDESTRUCTIBILITY_POTION;

    private static Potion registerPotion(String name) {
        return Registry.register(Registry.POTION, new Identifier(TitanFabric.MOD_ID, name),
                new Potion(new StatusEffectInstance(TitanFabricStatusEffects.INDESTRUCTIBILITY, 200, 0)));
    }

    public static void registerAllPotions() {
        INDESTRUCTIBILITY_POTION = registerPotion("indestructibility_potion");  //TODO: Potion crafting

    }
}
