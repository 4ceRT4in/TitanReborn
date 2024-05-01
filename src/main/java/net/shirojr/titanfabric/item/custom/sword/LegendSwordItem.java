package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.SwordType;

public class LegendSwordItem extends TitanFabricSwordItem {
    public LegendSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed,
                           SwordType swordType, Item.Settings settings) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, swordType, null, settings);
    }
}
