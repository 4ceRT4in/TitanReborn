package net.shirojr.titanfabric.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.Anvilable;
import net.shirojr.titanfabric.util.items.ToolTipHelper;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanFabricSwordItem extends SwordItem implements WeaponEffectCrafting, Anvilable {
    protected final boolean canHaveWeaponEffects;
    private final WeaponEffect baseEffect;
    private final SwordType swordType;

    public TitanFabricSwordItem(boolean canHaveWeaponEffects, ToolMaterial toolMaterial, int attackDamage,
                                float attackSpeed, SwordType swordType, WeaponEffect baseEffect, Item.Settings settings) {
        super(toolMaterial, settings.attributeModifiers(SwordItem.createAttributeModifiers(toolMaterial, attackDamage, attackSpeed)));
        this.canHaveWeaponEffects = canHaveWeaponEffects;
        this.baseEffect = baseEffect;
        this.swordType = swordType;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        if (this.getBaseEffect() != null) {
            EffectHelper.applyEffectToStack(stack, this.getBaseEffect(), false);
        }
        return stack;
    }

    public boolean canHaveWeaponEffects() {
        return this.canHaveWeaponEffects;
    }

    public float getCritMultiplier() {
        return this.swordType.getCritMultiplier();
    }

    public int getCooldownTicks() {
        return this.swordType.getCooldownTicks();
    }

    @Nullable
    @Override
    public WeaponEffectData getBaseEffect() {
        if (this.baseEffect == null) return null;
        return new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, this.baseEffect, 1);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ToolTipHelper.appendSwordToolTip(tooltip, stack);
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        EffectHelper.applyWeaponEffectsOnTarget(target.getWorld(), stack, target);
        if (this.baseEffect != null) {
            EffectHelper.applyWeaponEffectsOnTarget(target.getWorld(), stack, target);
        }

        return super.postHit(stack, target, attacker);
    }

    public SwordType getSwordType() {
        return this.swordType;
    }
}
