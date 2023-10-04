package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
/*    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canFoodHeal()Z", ordinal = 1))
    private boolean titanfabric$heal(PlayerEntity instance) {
        int foodLevel = instance.getHungerManager().getFoodLevel();

        return !ConfigInit.CONFIG.restrictFoodHealing && foodLevel >= 18 &&
                instance.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
    }*/
    //TODO: implement gamerule
}
