package net.shirojr.titanfabric.item.custom;


import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.List;

public class TitanFabricShieldItem extends ShieldItem implements Anvilable {

    private final Item[] repairItems;

    public TitanFabricShieldItem(Item.Settings settings, Item... repairItems) {
        super(settings);
        this.repairItems = repairItems;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (stack.getItem() == TitanFabricItems.NETHERITE_SHIELD) {
            TitanFabricDyeProviders.applyExtendedTooltip(tooltip,"tooltip.titanfabric.netherite_shield");
        }
        if (stack.getItem() == TitanFabricItems.LEGEND_SHIELD) {
            tooltip.add(Text.translatable("tooltip.titanfabric.legendArmorHealth", 4.0).formatted(Formatting.DARK_PURPLE));
        }
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

