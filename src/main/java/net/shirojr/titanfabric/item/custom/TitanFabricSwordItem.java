package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanFabricSwordItem extends SwordItem {
    private final boolean isGreatsword;
    public TitanFabricSwordItem(boolean isGreatsword, ToolMaterial toolMaterial, int attackDamage, float attackSpeed) {
        super(toolMaterial, attackDamage, attackSpeed, new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
        this.isGreatsword = isGreatsword;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, EffectHelper.appendToolTip(tooltip, stack), context);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        EffectHelper.applyWeaponEffectOnTarget(target.getWorld(), stack, attacker, target);
        return super.postHit(stack, target, attacker);
    }
}
