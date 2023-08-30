package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.shirojr.titanfabric.init.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canFoodHeal()Z", ordinal = 1))
    public boolean titanfabric$heal(PlayerEntity instance) {
        int foodLevel = instance.getHungerManager().getFoodLevel();

        return !ConfigInit.CONFIG.restrictFoodHealing && foodLevel >= 18 &&
                instance.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
    }
}
