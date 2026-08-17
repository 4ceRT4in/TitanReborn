package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class EnchantedDiamondAppleItem extends DiamondAppleItem {
    private static final int REGENERATION_DURATION = 200;

    public EnchantedDiamondAppleItem(Settings settings) {
        super(settings, 0, 1800);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        consumeFood(world, user, stack);

        int useIndex = Math.min(2, stack.getDamage());
        if (!world.isClient()) {
            applyDiamondAbsorption(user, getAmplifierForUse(useIndex), getDurationForUse(useIndex));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, REGENERATION_DURATION, 2));
        }

        if (user instanceof PlayerEntity player && player.getAbilities().creativeMode) {
            return stack;
        }

        int newDamage = stack.getDamage() + 1;
        if (newDamage >= stack.getMaxDamage()) {
            return ItemStack.EMPTY;
        }

        stack.setDamage(newDamage);
        return stack;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x4ACBFF;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        int remainingUses = Math.max(0, stack.getMaxDamage() - stack.getDamage());
        tooltip.add(Text.translatable("tooltip.titanfabric.enchanted_diamond_apple.uses", remainingUses).formatted(Formatting.GRAY));
    }

    private static int getAmplifierForUse(int useIndex) {
        return switch (useIndex) {
            case 0 -> 1;
            case 1 -> 2;
            default -> 3;
        };
    }

    private static int getDurationForUse(int useIndex) {
        return switch (useIndex) {
            case 0 -> 4800;
            case 1 -> 6000;
            default -> 7200;
        };
    }
}
