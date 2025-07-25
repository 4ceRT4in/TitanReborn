package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.VariationHolder;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

import java.util.List;

public class CitrinSwordItem extends TitanFabricSwordItem implements VariationHolder {
    public CitrinSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed,
                           SwordType swordType, Item.Settings settings) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, swordType, WeaponEffect.POISON, settings);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.titanfabric.citrinSword"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public List<ItemStack> getVariations() {
        return EffectHelper.generateSwordsStacks(this, true);
    }
}
