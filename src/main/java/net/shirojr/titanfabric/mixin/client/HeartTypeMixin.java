package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.shirojr.titanfabric.util.HeartsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.client.gui.hud.InGameHud.HeartType.CONTAINER;
import static net.minecraft.client.gui.hud.InGameHud.HeartType.NORMAL;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.HeartType.class)
public abstract class HeartTypeMixin {

    @Inject(method = "getU", at = @At("HEAD"), cancellable = true)
    private void getU(boolean halfHeart, boolean blinking, CallbackInfoReturnable<Integer> cir) {
        InGameHud.HeartType self = (InGameHud.HeartType)(Object)this;
        int i;
        if (self == CONTAINER) {
            i = blinking ? 1 : 0;
        } else {
            int j = halfHeart ? 1 : 0;
            int k = self.hasBlinkingTexture && blinking ? 2 : 0;
            i = j + k;
        }
        if (self == NORMAL && HeartsManager.isFrozenHearts()) {
            cir.setReturnValue(16 + (9 * 2 + i) * 9);
        }
    }
}
