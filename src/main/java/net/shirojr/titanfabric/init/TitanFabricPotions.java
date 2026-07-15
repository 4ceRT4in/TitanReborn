package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricPotions {
    public static RegistryEntry<Potion> EMBER_BURNING =
            registerPotion("ember_burning", TitanFabricStatusEffects.EMBER_BURNING, 1, 0, Potions.AWKWARD, Items.BLAZE_POWDER);
    /** Separate registered variants keep brewing, splash and lingering contents identical. */
    public static RegistryEntry<Potion> STRONG_FIRE_RESISTANCE =
            registerPotion("strong_fire_resistance", "fire_resistance", StatusEffects.FIRE_RESISTANCE, 900, 1, Potions.FIRE_RESISTANCE, Items.GLOWSTONE_DUST);
    public static RegistryEntry<Potion> STRONG_WEAKNESS =
            registerPotion("strong_weakness", "weakness", StatusEffects.WEAKNESS, 300, 3, Potions.WEAKNESS, Items.GLOWSTONE_DUST);
    public static RegistryEntry<Potion> INDESTRUCTIBLE_POTION =
            registerPotion("indestructibility_potion", TitanFabricStatusEffects.INDESTRUCTIBILITY,
                    1800, 0, Potions.AWKWARD, TitanFabricBlocks.LEGEND_CRYSTAL.asItem());
    public static RegistryEntry<Potion> LONG_INDESTRUCTIBLE_POTION =
            registerPotion("long_indestructibility_potion", TitanFabricStatusEffects.INDESTRUCTIBILITY,
                    3600, 0, INDESTRUCTIBLE_POTION, Items.REDSTONE);

    public static RegistryEntry<Potion> FROSTBURN_POTION =
            registerPotion("frostburn_potion", TitanFabricStatusEffects.FROSTBURN,
                    400, 6, Potions.AWKWARD, Items.SNOWBALL);
    public static RegistryEntry<Potion> STRONG_FROSTBURN_POTION =
            registerPotion("strong_frostburn_potion", TitanFabricStatusEffects.FROSTBURN,
                    300, 12, FROSTBURN_POTION, Items.GLOWSTONE_DUST);
    public static RegistryEntry<Potion> LONG_FROSTBURN_POTION =
            registerPotion("long_frostburn_potion", TitanFabricStatusEffects.FROSTBURN,
                    600, 6, FROSTBURN_POTION, Items.REDSTONE);

    public static RegistryEntry<Potion> IMMUNITY_POTION =
            registerPotion("immunity_potion", TitanFabricStatusEffects.IMMUNITY,
                    1800, 0, Potions.AWKWARD, TitanFabricItems.CITRIN_STAR);
    public static RegistryEntry<Potion> LONG_IMMUNITY_POTION =
            registerPotion("long_immunity_potion", TitanFabricStatusEffects.IMMUNITY,
                    3600, 0, IMMUNITY_POTION, Items.REDSTONE);
    public static RegistryEntry<Potion> RECOVERY_POTION =
            registerPotion("recovery_potion", TitanFabricStatusEffects.RECOVERY,
                    900, 0, Potions.AWKWARD, Items.GOLDEN_APPLE);
    public static RegistryEntry<Potion> STRONG_RECOVERY_POTION =
            registerPotion("strong_recovery_potion", TitanFabricStatusEffects.STRONG_RECOVERY,
                    600, 0, RECOVERY_POTION, Items.GLOWSTONE_DUST);
    public static RegistryEntry<Potion> LONG_RECOVERY_POTION =
            registerPotion("long_recovery_potion", TitanFabricStatusEffects.LONG_RECOVERY,
                    1800, 0, RECOVERY_POTION, Items.REDSTONE);

    public static RegistryEntry<Potion> registerPotion(String id, RegistryEntry<StatusEffect> effect, int duration, int amplifier, RegistryEntry<Potion> input, Item ingredient) {
        return registerPotion(id, null, effect, duration, amplifier, input, ingredient);
    }

    public static RegistryEntry<Potion> registerPotion(String id, String baseName, RegistryEntry<StatusEffect> effect, int duration, int amplifier, RegistryEntry<Potion> input, Item ingredient) {
        Potion potion = Registry.register(Registries.POTION, TitanFabric.getId(id), new Potion(baseName, new StatusEffectInstance(effect, duration, amplifier)));
        RegistryEntry<Potion> entry = Registries.POTION.getEntry(potion);
        registerRecipes(input, ingredient, entry);
        return entry;
    }

    public static void registerRecipes(RegistryEntry<Potion> input, Item ingredient, RegistryEntry<Potion> output) {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> builder.registerPotionRecipe(input, ingredient, output));
    }

    public static void initialize() {
        // static initialisation
    }
}
