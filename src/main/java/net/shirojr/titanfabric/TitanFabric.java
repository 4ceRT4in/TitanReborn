package net.shirojr.titanfabric;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.effect.potion.TitanFabricPotions;
import net.shirojr.titanfabric.init.ConfigInit;
import net.shirojr.titanfabric.item.TitanFabricItems;
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

        ConfigInit.init();
        TitanFabricItems.registerModItems();
        TitanFabricBlocks.registerModBlocks();

        TitanFabricStatusEffects.registerStatusEffects();
        TitanFabricPotions.registerAllPotions();

        TitanFabricWorldGen.generateTitanFabricWorldGen();

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