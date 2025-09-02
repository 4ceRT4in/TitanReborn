package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.VariationHolder;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmberSwordItem extends TitanFabricSwordItem implements VariationHolder {
    public EmberSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed,
                          SwordType swordType, Item.Settings settings) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, swordType, WeaponEffect.FIRE, settings);
        settings.component(TitanFabricDataComponents.WEAPON_EFFECTS, new HashSet<>(Set.of(new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, WeaponEffect.FIRE, 1))));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.titanfabric.emberSword"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public List<ItemStack> getVariations() {
        return EffectHelper.generateSwordsStacks(this, true);
    }
}
