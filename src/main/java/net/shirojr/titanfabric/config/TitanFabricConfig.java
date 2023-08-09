package net.shirojr.titanfabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "titanfabric")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class TitanFabricConfig implements ConfigData {
    @Comment("Change value of WEAKNESS StatusEffect")
    public double weaknessStatusEffectModifier = -4.0;
    public double TitanArmorHelmetHealth = 2.0;
    public double TitanArmorChestplateHealth = 4.0;
    public double TitanArmorLeggingsHealth = 3.0;
    public double TitanArmorBootsHealth = 1.0;
    public int netheriteSwordAttackDamage = 8;
    public float netheriteSwordAttackSpeed = -2.4f;
    public int netheriteSwordMaxDamage = 2031;
}
