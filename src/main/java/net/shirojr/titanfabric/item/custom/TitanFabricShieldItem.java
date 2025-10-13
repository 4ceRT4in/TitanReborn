package net.shirojr.titanfabric.item.custom;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.List;

public class TitanFabricShieldItem extends ShieldItem implements Anvilable {

    private final Item[] repairItems;
    private final int cooldown;

    public TitanFabricShieldItem(Settings settings, int cooldown, Item... repairItems) {
        super(settings);
        this.cooldown = cooldown;
        this.repairItems = repairItems;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.titanfabric.attribute.shieldCooldown", getCooldown()));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        if(stack.getItem() == TitanFabricItems.LEGEND_SHIELD) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 10, 0, false, false));
        }
        return 72000;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        for (Item repairItem : repairItems) {
            if (ingredient.getItem() == repairItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}

