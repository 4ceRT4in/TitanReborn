package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {

    @Shadow
    private int foodLevel;
    @Shadow
    private float saturationLevel;
    @Shadow
    private float exhaustion;
    @Shadow
    private int foodTickTimer;
    @Shadow
    private int prevFoodLevel;

    @Shadow
    public abstract void addExhaustion(float exhaustion);

    // method from Globox1997
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void updateMixin(PlayerEntity player, CallbackInfo info) {
        if (player.getWorld().getGameRules().getBoolean(TitanFabricGamerules.LEGACY_FOOD_REGENERATION)) {
            Difficulty difficulty = player.getWorld().getDifficulty();
            this.prevFoodLevel = this.foodLevel;
            if (this.exhaustion > 4.0f) {
                this.exhaustion -= 4.0f;
                if (this.saturationLevel > 0.0f) {
                    this.saturationLevel = Math.max(this.saturationLevel - 1.0f, 0.0f);
                } else if (difficulty != Difficulty.PEACEFUL) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }
            if (player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION) && this.foodLevel >= 18 && player.canFoodHeal()) {
                ++this.foodTickTimer;
                int requiredFoodTick = 80;
                StatusEffectInstance witherEffectInstance = player.getStatusEffect(StatusEffects.WITHER);
                if (witherEffectInstance != null) {
                    requiredFoodTick = 80 + 8 * witherEffectInstance.getAmplifier();
                }
                if (this.foodTickTimer >= requiredFoodTick) {
                    healWithFrostburnLimit(player, 1.0f);
                    // player.heal(1.0f);
                    this.addExhaustion(3.0f);
                    this.foodTickTimer = 0;
                }
            } else if (this.foodLevel <= 0) {
                ++this.foodTickTimer;
                if (this.foodTickTimer >= 80) {
                    if (player.getHealth() > 10.0f || difficulty == Difficulty.HARD || player.getHealth() > 1.0f && difficulty == Difficulty.NORMAL) {
                        player.damage(player.getDamageSources().starve(), 1.0f);
                    }
                    this.foodTickTimer = 0;
                }
            } else {
                this.foodTickTimer = 0;
            }
            info.cancel();
        }
    }

    // This method is not working with Globox's update cancellation...
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private void healWithFrostburnPrevention(PlayerEntity instance, float v, Operation<Void> original) {
        FrostburnComponent frostburnComponent = FrostburnComponent.get(instance);
        float limitedNewHealth = Math.min(frostburnComponent.getMissingHealth(), v);
        if (limitedNewHealth <= 0) return;
        original.call(instance, v);
    }

    @SuppressWarnings("SameParameterValue")
    @Unique
    private void healWithFrostburnLimit(PlayerEntity player, float amount) {
        FrostburnComponent frostburnComponent = FrostburnComponent.get(player);
        float limitedNewHealth = Math.min(frostburnComponent.getMissingHealth(), amount);
        if (limitedNewHealth <= 0) return;
        player.heal(limitedNewHealth);
    }
}
