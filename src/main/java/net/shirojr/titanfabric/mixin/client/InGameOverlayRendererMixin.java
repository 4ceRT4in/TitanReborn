package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabricClient;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @Unique
    private static final SpriteIdentifier TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/soul_fire_1"));

    @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnFire()Z"), cancellable = true)
    private static void fireOverlayMixin(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo info) {
        if (minecraftClient.player == null) return;
        int count = 0;
        for (Item entry : ArmorHelper.getArmorItems(minecraftClient.player)) {
            if (entry instanceof EmberArmorItem) count++;
        }
        if (count >= 4) info.cancel();
    }
    @Redirect(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;"))
    private static Sprite getSprite(SpriteIdentifier obj, MinecraftClient client) {
        if (TitanFabricClient.soulFireEntities.contains(client.player.getUuid())) {
            return TEXTURE.getSprite();
        }
        return obj.getSprite();
    }
}