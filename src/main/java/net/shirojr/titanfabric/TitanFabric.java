package net.shirojr.titanfabric;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.titanfabric.item.TitanFabricItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitanFabric implements ModInitializer {
    public static final String MOD_ID = "titanfabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        TitanFabricItems.registerModItems();

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