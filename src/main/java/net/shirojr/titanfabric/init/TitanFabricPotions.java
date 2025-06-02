package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricPotions {
    public static RegistryEntry<Potion> INDESTRUCTIBLE_POTION =
            registerPotion("indestructibility_potion", TitanFabricStatusEffects.INDESTRUCTIBILITY,
                    3600, 0, Potions.AWKWARD, TitanFabricBlocks.LEGEND_CRYSTAL.asItem());
    public static RegistryEntry<Potion> LONG_INDESTRUCTIBLE_POTION =
            registerPotion("long_indestructibility_potion", TitanFabricStatusEffects.INDESTRUCTIBILITY,
                    9600, 0, INDESTRUCTIBLE_POTION, Items.REDSTONE);

    public static RegistryEntry<Potion> registerPotion(String id, RegistryEntry<StatusEffect> effect, int duration, int amplifier, RegistryEntry<Potion> input, Item ingredient) {
        Potion potion = Registry.register(Registries.POTION, TitanFabric.getId(id), new Potion(new StatusEffectInstance(effect, duration, amplifier)));
        RegistryEntry<Potion> entry = Registries.POTION.getEntry(potion);
        registerRecipes(input, ingredient, entry);
        return entry;
    }

    public static void registerRecipes(RegistryEntry<Potion> input, Item ingredient, RegistryEntry<Potion> output) {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(input, ingredient, output);
        });
    }

    public static void initialize() {
        // static initialisation
    }
}
