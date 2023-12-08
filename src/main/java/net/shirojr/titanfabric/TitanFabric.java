package net.shirojr.titanfabric;

import net.fabricmc.api.ModInitializer;
import net.shirojr.titanfabric.block.TitanFabricBlockEntities;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.block.stats.TitanFabricStats;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.event.CommandRegistrationEvent;
import net.shirojr.titanfabric.gamerule.TitanFabricGamerules;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.network.TitanFabricNetworking;
import net.shirojr.titanfabric.potion.TitanFabricPotions;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.world.feature.TitanFabricConfiguredFeatures;
import net.shirojr.titanfabric.world.gen.TitanFabricWorldGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitanFabric implements ModInitializer {

    public static final String MODID = "titanfabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        TitanFabricConfiguredFeatures.registerConfiguredFeatures();
        TitanFabricItems.registerModItems();
        TitanFabricRecipes.registerModRecipes();
        TitanFabricBlocks.registerModBlocks();
        TitanFabricBlockEntities.registerBlockEntities();
        TitanFabricScreenHandlers.register();
        TitanFabricStatusEffects.registerStatusEffects();
        TitanFabricPotions.register();
        TitanFabricWorldGen.generateWorldGen();
        TitanFabricNetworking.registerServerReceivers();
        TitanFabricGamerules.register();
        TitanFabricStats.register();

        CommandRegistrationEvent.register();

        LOGGER.info("Initialized all " + MODID + " common components");
        LoggerUtil.devLogger("Initialized Instance in development environment!");
    }
}