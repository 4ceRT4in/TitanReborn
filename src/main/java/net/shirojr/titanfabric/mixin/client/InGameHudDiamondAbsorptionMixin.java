package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.cca.component.DiamondAbsorptionComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudDiamondAbsorptionMixin {
    @Unique
    private int titanfabric$totalAbsorptionHeartsToRender;
    @Unique
    private int titanfabric$visibleDiamondAbsorptionHalfHearts;
    @Unique
    private int titanfabric$absorptionHeartDrawCalls;
    @Unique
    private int titanfabric$currentDiamondHeartMode;

    @Inject(method = "renderHealthBar", at = @At("HEAD"))
    private void titanfabric$prepareDiamondAbsorptionHearts(DrawContext context, PlayerEntity player, int x, int y,
                                                            int lines, int regeneratingHeartIndex, float maxHealth,
                                                            int lastHealth, int health, int absorption, boolean blinking,
                                                            CallbackInfo ci) {
        float visibleDiamondAbsorption = Math.min(DiamondAbsorptionComponent.get(player).getDiamondAbsorptionAmount(), absorption);
        this.titanfabric$totalAbsorptionHeartsToRender = MathHelper.ceil(absorption / 2.0f);
        this.titanfabric$visibleDiamondAbsorptionHalfHearts = MathHelper.ceil(visibleDiamondAbsorption);
        this.titanfabric$absorptionHeartDrawCalls = 0;
        this.titanfabric$currentDiamondHeartMode = 0;
    }

    @Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
    private void titanfabric$replaceAbsorptionHeartTexture(DrawContext context, InGameHud.HeartType type, int x, int y,
                                                           boolean hardcore, boolean blinking, boolean half, CallbackInfo ci) {
        this.titanfabric$currentDiamondHeartMode = 0;
        if (type != InGameHud.HeartType.ABSORBING) return;
        this.titanfabric$currentDiamondHeartMode = titanfabric$getDiamondHeartMode();
        if (this.titanfabric$currentDiamondHeartMode != 2) return;

        Identifier texture = TitanFabric.getId("hud/heart/diamond_absorption_" + (half ? "half" : "full"));
        context.drawGuiTexture(texture, x, y, 0, 9, 9);
        ci.cancel();
    }

    @Inject(method = "drawHeart", at = @At("TAIL"))
    private void titanfabric$overlayHalfDiamondHeart(DrawContext context, InGameHud.HeartType type, int x, int y,
                                                     boolean hardcore, boolean blinking, boolean half, CallbackInfo ci) {
        if (type != InGameHud.HeartType.ABSORBING) return;
        if (this.titanfabric$currentDiamondHeartMode != 1) return;

        Identifier texture = TitanFabric.getId("hud/heart/diamond_absorption_half");
        context.drawGuiTexture(texture, x, y, 0, 9, 9);
    }

    @Unique
    private int titanfabric$getDiamondHeartMode() {
        int drawIndex = this.titanfabric$absorptionHeartDrawCalls++;
        int slotIndexFromLeft = this.titanfabric$totalAbsorptionHeartsToRender - drawIndex - 1;
        int remainingBlueHalfHearts = this.titanfabric$visibleDiamondAbsorptionHalfHearts - (slotIndexFromLeft * 2);
        if (remainingBlueHalfHearts >= 2) {
            return 2;
        }
        if (remainingBlueHalfHearts == 1) {
            return 1;
        }
        return 0;
    }
}
