package net.shirojr.titanfabric.block.stats;

import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricStats {
    public static final Identifier USED_ADVANCED_ANVIL = registerDefaultStat("used_advanced_anvil");

    private static Identifier registerDefaultStat(String name) {
        Identifier identifier = new Identifier(TitanFabric.MODID, name);
        Registry.register(Registry.CUSTOM_STAT, name, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, StatFormatter.DEFAULT);
        return identifier;
    }

    public static void register() { }
}
