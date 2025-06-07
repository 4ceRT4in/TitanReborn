package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.VariationHolder;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;

import java.util.List;

public class DiamondSwordItem extends TitanFabricSwordItem implements WeaponEffectCrafting, VariationHolder {
    private final boolean registerBaseVersion;

    public DiamondSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed,
                            SwordType swordType, WeaponEffect baseEffect, Settings settings, boolean registerBaseVersion) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, swordType, baseEffect, settings);
        this.registerBaseVersion = registerBaseVersion;
    }

    @Override
    public List<ItemStack> getVariations() {
        return EffectHelper.generateSwordsStacks(this, registerBaseVersion);
    }
}
