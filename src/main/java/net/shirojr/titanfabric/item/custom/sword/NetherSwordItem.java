package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

public class NetherSwordItem extends TitanFabricSwordItem {
    public NetherSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, WeaponEffect.FIRE, settings);
    }
}
