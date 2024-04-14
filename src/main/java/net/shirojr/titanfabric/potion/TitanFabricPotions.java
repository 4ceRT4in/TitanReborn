package net.shirojr.titanfabric.potion;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.mixin.BrewingRecipeRegistryInvoker;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricPotions {
    public static Potion INDESTRUCTIBLE_POTION =
            registerPotion("indestructibility_potion", TitanFabricStatusEffects.INDESTRUCTIBILITY,
                    3600, 0, Potions.AWKWARD, TitanFabricItems.LEGEND_CRYSTAL);
    public static Potion LONG_INDESTRUCTIBLE_POTION =
            registerPotion("long_indestructibility_potion", TitanFabricStatusEffects.INDESTRUCTIBILITY,
                    9600, 0, INDESTRUCTIBLE_POTION, Items.REDSTONE);

    public static Potion registerPotion(String id, StatusEffect effect, int duration, int amplifier, Potion input, Item ingredient) {
        Potion potion = new Potion(new StatusEffectInstance(effect, duration, amplifier));
        registerRecipes(input, ingredient, potion);
        return Registry.register(Registry.POTION, new Identifier(TitanFabric.MODID, id), potion);
    }

    public static void registerRecipes(Potion input, Item ingredient, Potion output) {
        BrewingRecipeRegistryInvoker.invokeRegisterPotionRecipe(input, ingredient, output);
    }

    public static void register() {
        LoggerUtil.devLogger("initializing potions");
    }
}
