package net.shirojr.titanfabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "titanfabric")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class TitanFabricConfig implements ConfigData {
    @Comment("Change value of WEAKNESS StatusEffect")
    public double weaknessStatusEffectModifier = -4.0;
}
