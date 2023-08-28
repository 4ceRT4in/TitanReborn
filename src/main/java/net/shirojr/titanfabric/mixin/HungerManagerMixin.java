package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.shirojr.titanfabric.init.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canFoodHeal()Z"))
    public boolean titanFabric$heal(PlayerEntity instance) {
        if (!ConfigInit.CONFIG.canFoodHeal) return false;
        else return instance.getHealth() > 0.0f && instance.getHealth() < instance.getMaxHealth();
    }
}
