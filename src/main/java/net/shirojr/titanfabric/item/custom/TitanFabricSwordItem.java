package net.shirojr.titanfabric.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.ModelPredicateProviders;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class TitanFabricSwordItem extends SwordItem {
    private final boolean isGreatsword;
    private final WeaponEffects baseEffect;
    public TitanFabricSwordItem(boolean isGreatsword, ToolMaterial toolMaterial, int attackDamage, float attackSpeed, WeaponEffects baseEffect) {
        super(toolMaterial, attackDamage, attackSpeed, new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
        this.isGreatsword = isGreatsword;
        this.baseEffect = baseEffect;
        if (isClientSide()) {
            ModelPredicateProviders.registerEffectProvider(TitanFabricItems.CITRIN_SWORD, new Identifier("effect"));
            ModelPredicateProviders.registerStrengthProvider(TitanFabricItems.CITRIN_SWORD, new Identifier("strength"));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (EffectHelper.stackHasWeaponEffect(stack)) {
            super.appendTooltip(stack, world, EffectHelper.appendToolTip(tooltip, stack), context);
            return;
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        //Apply extra effects
        EffectHelper.applyWeaponEffectOnTargetFromNBT(target.getWorld(), stack, attacker, target);

        //Apply base effect
        if(this.baseEffect != null) {
            EffectHelper.applyWeaponEffectOnTarget(this.baseEffect, 1 , target.getWorld(), stack, attacker, target);
        }

        return super.postHit(stack, target, attacker);
    }

    @Environment(EnvType.CLIENT)
    private boolean isClientSide() {
        return true;
    }

}
