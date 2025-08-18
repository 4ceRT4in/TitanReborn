package net.shirojr.titanfabric.render.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;

public class FrostburnHudRenderer implements HudRenderCallback {
    public static final int SPRITE_SIZE = 9;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        FrostburnComponent frostburnComponent = FrostburnComponent.get(player);
        int x = context.getScaledWindowWidth() / 2;
        int y = context.getScaledWindowHeight() / 2;
        float frostburn = frostburnComponent.getFrostburn();
        for (int i = 0; i < Math.floor(frostburn); i++) {
            // drawHeart(context, x, y, );
        }
    }

    private static void drawHeart(DrawContext context, int x, int y, boolean half) {
        RenderSystem.enableBlend();
        Identifier fullTexture = Identifier.ofVanilla("hud/heart/frozen_" + (half ? "half" : "full"));
        context.drawGuiTexture(fullTexture, x, y, SPRITE_SIZE, SPRITE_SIZE);
        RenderSystem.disableBlend();
    }
}
