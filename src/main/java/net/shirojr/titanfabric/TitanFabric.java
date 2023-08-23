package net.shirojr.titanfabric;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.titanfabric.block.TitanFabricBlockEntities;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.effect.potion.TitanFabricPotions;
import net.shirojr.titanfabric.enchant.TitanFabricEnchantments;
import net.shirojr.titanfabric.init.ConfigInit;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.network.TitanFabricNetworking;
import net.shirojr.titanfabric.recipe.TitanFabricRecipies;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.world.feature.TitanFabricConfiguredFeatures;
import net.shirojr.titanfabric.world.gen.TitanFabricWorldGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitanFabric implements ModInitializer {

    public static final String MODID = "titanfabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        ConfigInit.init();
        TitanFabricConfiguredFeatures.registerConfiguredFeatures();
        TitanFabricEnchantments.registerModEnchantments();
        TitanFabricItems.registerModItems();
        TitanFabricRecipies.registerModRecipies();
        TitanFabricBlocks.registerModBlocks();
        TitanFabricBlockEntities.registerBlockEntities();
        TitanFabricScreenHandlers.registerAllScreenHandlers();
        TitanFabricStatusEffects.registerStatusEffects();
        TitanFabricPotions.registerAllPotions();
        TitanFabricWorldGen.generateWorldGen();
        TitanFabricNetworking.registerServerReceivers();
    }

    /**
     * Uses LOGGER only in a development environment
     * @param input input String for the LOGGER
     */
    public static void devLogger(String input) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.info("[dev] - " + input);
        }
    }
}