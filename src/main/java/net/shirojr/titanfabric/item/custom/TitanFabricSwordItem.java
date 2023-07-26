package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class TitanFabricSwordItem extends SwordItem {
    private final boolean hasWeaponEffects;
    private final WeaponEffects baseEffect;

    public TitanFabricSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed, WeaponEffects baseEffect) {
        super(toolMaterial, attackDamage, attackSpeed, new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
        this.hasWeaponEffects = hasWeaponEffects;
        this.baseEffect = baseEffect;
    }

    public boolean hasWeaponEffects() {
        return this.hasWeaponEffects;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.hasWeaponEffects) {
            TitanFabric.devLogger("Registering Weapon Effects for " + this.getDefaultStack().getName());
            super.appendStacks(group, EffectHelper.generateAllEffectVersionStacks(this, stacks));
        } else super.appendStacks(group, stacks);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!EffectHelper.stackHasWeaponEffect(stack)) super.appendTooltip(stack, world, tooltip, context);
        super.appendTooltip(stack, world, EffectHelper.appendToolTip(tooltip, stack), context);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        //Apply extra effects
        EffectHelper.applyWeaponEffectOnTargetFromNBT(target.getWorld(), stack, attacker, target);

        //Apply base effect
        if (this.baseEffect != null) {
            EffectHelper.applyWeaponEffectOnTarget(this.baseEffect, 1, target.getWorld(), stack, attacker, target);
        }

        return super.postHit(stack, target, attacker);
    }
}
