package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.collection.DefaultedList;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

public class DiamondSwordItem extends TitanFabricSwordItem {
    public DiamondSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed, WeaponEffect baseEffect, Settings settings) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, baseEffect, settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (!isIn(group)) return;
        if (this.canHaveWeaponEffects) {
            EffectHelper.generateAllEffectVersionStacks(this, stacks, false);
        } else {
            stacks.add(new ItemStack(this));
        }
    }
}
