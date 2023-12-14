package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.shirojr.titanfabric.gamerule.TitanFabricGamerules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void updateMixin(PlayerEntity player, CallbackInfo info) {
        if (player.getWorld().getGameRules().getBoolean(TitanFabricGamerules.LEGACY_FOOD_REGENERATION)) {
            Difficulty difficulty = player.world.getDifficulty();
            this.prevFoodLevel = this.foodLevel;
            if (this.exhaustion > 4.0f) {
                this.exhaustion -= 4.0f;
                if (this.saturationLevel > 0.0f) {
                    this.saturationLevel = Math.max(this.saturationLevel - 1.0f, 0.0f);
                } else if (difficulty != Difficulty.PEACEFUL) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }
            if (player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION) && this.foodLevel >= 18 && player.canFoodHeal()) {
                ++this.foodTickTimer;
                int requiredFoodTick = 80;
                if (player.hasStatusEffect(StatusEffects.WITHER)) {
                    requiredFoodTick = 80 + 8 * player.getStatusEffect(StatusEffects.WITHER).getAmplifier();
                }
                if (this.foodTickTimer >= requiredFoodTick) {
                    player.heal(1.0f);
                    this.addExhaustion(3.0f);
                    this.foodTickTimer = 0;
                }
            } else if (this.foodLevel <= 0) {
                ++this.foodTickTimer;
                if (this.foodTickTimer >= 80) {
                    if (player.getHealth() > 10.0f || difficulty == Difficulty.HARD || player.getHealth() > 1.0f && difficulty == Difficulty.NORMAL) {
                        player.damage(DamageSource.STARVE, 1.0f);
                    }
                    this.foodTickTimer = 0;
                }
            } else {
                this.foodTickTimer = 0;
            }
            info.cancel();
        }
    }

    @Shadow
    public void addExhaustion(float exhaustion) {
    }
}
