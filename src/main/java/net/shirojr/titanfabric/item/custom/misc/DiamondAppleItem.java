package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shirojr.titanfabric.cca.component.DiamondAbsorptionComponent;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.util.effects.DiamondAbsorptionHelper;

public class DiamondAppleItem extends Item {
    private static final int REGENERATION_DURATION = 100;
    private static final int REGENERATION_AMPLIFIER = 1;

    private final int diamondAbsorptionAmplifier;
    private final int diamondAbsorptionDuration;

    public DiamondAppleItem(Settings settings, int diamondAbsorptionAmplifier, int diamondAbsorptionDuration) {
        super(settings);
        this.diamondAbsorptionAmplifier = diamondAbsorptionAmplifier;
        this.diamondAbsorptionDuration = diamondAbsorptionDuration;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        consumeFood(world, user, stack);

        if (!world.isClient()) {
            applyDiamondAbsorption(user, this.diamondAbsorptionAmplifier, this.diamondAbsorptionDuration);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, REGENERATION_DURATION, REGENERATION_AMPLIFIER));
        }

        stack.decrementUnlessCreative(1, user);
        return stack;
    }

    protected void consumeFood(World world, LivingEntity user, ItemStack stack) {
        FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
        if (foodComponent == null) return;
        user.eatFood(world, stack.copy(), foodComponent);
    }

    protected void applyDiamondAbsorption(LivingEntity user, int amplifier, int duration) {
        user.addStatusEffect(new StatusEffectInstance(TitanFabricStatusEffects.DIAMOND_ABSORPTION, duration, amplifier));
        DiamondAbsorptionHelper.applyMaxAbsorptionModifier(user, amplifier);

        float targetAbsorption = DiamondAbsorptionHelper.getAbsorptionAmount(amplifier);
        user.setAbsorptionAmount(Math.max(user.getAbsorptionAmount(), targetAbsorption));
        DiamondAbsorptionComponent component = DiamondAbsorptionComponent.get(user);
        float effectAmount = Math.min(user.getAbsorptionAmount(), targetAbsorption);
        component.setDiamondAbsorptionAmount(effectAmount, true);
        component.setEffectAbsorptionAmount(effectAmount, true);
    }
}
