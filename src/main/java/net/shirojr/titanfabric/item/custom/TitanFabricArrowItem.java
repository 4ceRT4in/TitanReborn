package net.shirojr.titanfabric.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.VariationHolder;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TitanFabricArrowItem extends ArrowItem implements WeaponEffectCrafting, VariationHolder {

    public TitanFabricArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public List<WeaponEffect> supportedEffects() {
        return Arrays.stream(WeaponEffect.values()).toList();
    }

    @Override
    public Text getName(ItemStack stack) {
        var data = stack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (data == null) return super.getName(stack);
        WeaponEffect weaponEffect = null;
        for (WeaponEffectData entry : data) {
            if (!entry.type().equals(WeaponEffectType.INNATE_EFFECT)) continue;
            weaponEffect = entry.weaponEffect();
            break;
        }
        if (weaponEffect == null) return super.getName(stack);
        return switch (weaponEffect) {
            case BLIND -> Text.translatable("item.titanfabric.blindness_arrow");
            case FIRE -> Text.translatable("item.titanfabric.fire_arrow");
            case POISON -> Text.translatable("item.titanfabric.poison_arrow");
            case WEAK -> Text.translatable("item.titanfabric.weakness_arrow");
            case WITHER -> Text.translatable("item.titanfabric.wither_arrow");
            default -> {
                LoggerUtil.devLogger("couldn't find weapon effect for arrow", true, null);
                yield Text.translatable("item.titanfabric.arrow");
            }
        };
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        HashSet<WeaponEffectData> data = stack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (data == null) {
            super.appendTooltip(stack, context, tooltip, type);
            return;
        }
        WeaponEffect weaponEffect = null;
        for (WeaponEffectData entry : data) {
            if (!entry.type().equals(WeaponEffectType.INNATE_EFFECT)) continue;
            weaponEffect = entry.weaponEffect();
            break;
        }
        if (weaponEffect == null) {
            super.appendTooltip(stack, context, tooltip, type);
            return;
        }
        String tooltipPrefix = "tooltip.titanfabric.";
        switch (weaponEffect) {
            case BLIND -> tooltipPrefix = tooltipPrefix + "blindnessArrowEffect";
            case FIRE -> tooltipPrefix = tooltipPrefix + "fireArrowEffect";
            case POISON -> tooltipPrefix = tooltipPrefix + "poisonArrowEffect";
            case WEAK -> tooltipPrefix = tooltipPrefix + "weaknessArrowEffect";
            case WITHER -> tooltipPrefix = tooltipPrefix + "witherArrowEffect";
        }
        tooltip.add(Text.translatable(tooltipPrefix));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        var comp = stack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if(comp == null) return super.createArrow(world, stack, shooter, shotFrom);
        WeaponEffect weaponEffect = null;
        for (WeaponEffectData entry : comp) {
            if (!entry.type().equals(WeaponEffectType.INNATE_EFFECT)) continue;
            weaponEffect = entry.weaponEffect();
            break;
        }
        if(weaponEffect == null) return super.createArrow(world, stack, shooter, shotFrom);
        return new TitanFabricArrowEntity(world, shooter, weaponEffect, stack);
    }

    @Override
    public List<ItemStack> getVariations() {
        return EffectHelper.generateArrowStacks(this, false);
    }
}
