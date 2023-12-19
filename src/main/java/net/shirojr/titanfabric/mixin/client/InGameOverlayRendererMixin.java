package net.shirojr.titanfabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.shirojr.titanfabric.item.TitanFabricItems;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {

    // From AdventureZ
    @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnFire()Z"), cancellable = true)
    private static void fireOverlayMixin(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo info) {
        if (minecraftClient.player.getEquippedStack(EquipmentSlot.CHEST).isOf(TitanFabricItems.NETHER_CHESTPLATE)
                && minecraftClient.player.getEquippedStack(EquipmentSlot.HEAD).getItem() == TitanFabricItems.NETHER_HELMET
                && minecraftClient.player.getEquippedStack(EquipmentSlot.LEGS).getItem() == TitanFabricItems.NETHER_LEGGINGS
                        & minecraftClient.player.getEquippedStack(EquipmentSlot.FEET).getItem() == TitanFabricItems.NETHER_BOOTS) {
            info.cancel();
        }
    }
}