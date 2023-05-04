package net.shirojr.titanfabric.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.shirojr.titanfabric.config.TitanFabricConfig;

public class ConfigInit {

    public static TitanFabricConfig CONFIG = new TitanFabricConfig();

    public static void init() {
        AutoConfig.register(TitanFabricConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TitanFabricConfig.class).getConfig();
    }
}
