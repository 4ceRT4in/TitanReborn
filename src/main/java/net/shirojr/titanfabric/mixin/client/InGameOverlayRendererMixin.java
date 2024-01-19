package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {

    // From AdventureZ
    @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnFire()Z"), cancellable = true)
    private static void fireOverlayMixin(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo info) {
        if (minecraftClient.player == null) return;
        int count = 0;
        for (Item entry : ArmorHelper.getArmorItems(minecraftClient.player)) {
            if (entry instanceof NetherArmorItem) count++;
        }
        if (count >= 4) info.cancel();
    }
}