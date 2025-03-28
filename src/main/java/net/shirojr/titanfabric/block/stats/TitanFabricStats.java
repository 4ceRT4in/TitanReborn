package net.shirojr.titanfabric.block.stats;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricStats {
    public static final Identifier USED_ADVANCED_ANVIL = registerDefaultStat("used_advanced_anvil");

    private static Identifier registerDefaultStat(String name) {
        Identifier identifier = TitanFabric.getId(name);
        Registry.register(Registries.CUSTOM_STAT, name, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, StatFormatter.DEFAULT);
        return identifier;
    }

    public static void register() {
    }
}
