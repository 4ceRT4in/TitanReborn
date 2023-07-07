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

    public int woodenSwordAttackDamage = 3;
    public float woodenSwordAttackSpeed = -2.4f;
    public int woodenSwordMaxDamage = -1;

    public int goldenSwordAttackDamage = 3;
    public float goldenSwordAttackSpeed = -2.4f;
    public int goldenSwordMaxDamage = -1;

    public int stoneSwordAttackDamage = 4;
    public float stoneSwordAttackSpeed = -2.4f;
    public int stoneSwordMaxDamage = -1;

    public int ironSwordAttackDamage = 5;
    public float ironSwordAttackSpeed = -2.4f;
    public int ironSwordMaxDamage = -1;

    public int diamondSwordAttackDamage = 6;
    public float diamondSwordAttackSpeed = -2.4f;
    public int diamondSwordMaxDamage = -1;

    public int netheriteSwordAttackDamage = 8;
    public float netheriteSwordAttackSpeed = -2.4f;
    public int netheriteSwordMaxDamage = -1;
}
