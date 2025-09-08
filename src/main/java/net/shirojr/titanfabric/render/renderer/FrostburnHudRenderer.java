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
    public static final int SPACE_BETWEEN_SPRITES = 8;
    public static final int HEARTS_PER_ROW = 10;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        FrostburnComponent frostburnComponent = FrostburnComponent.get(player);
        float frostburn = frostburnComponent.getFrostburn();
        if (frostburn <= 0) return;

        int centerX = context.getScaledWindowWidth() / 2;
        int bottomY = context.getScaledWindowHeight();

        int x = centerX - 11;
        int y = bottomY - 39;

        int frostburnHearts = (int) Math.floor(frostburn / 2);
        boolean hasHalf = (frostburn % 1.0f) >= 0.5f;

        for (int i = 0; i < frostburnHearts; i++) {
            int heartX = x + (i % 10) * SPACE_BETWEEN_SPRITES;
            int heartY = y - (i / 10) * HEARTS_PER_ROW;

            drawHeart(context, heartX, heartY, false);
        }

        if (hasHalf) {
            int heartX = x + ((frostburnHearts + 1) % 10) * 8;
            int heartY = y - ((frostburnHearts + 1) / 10) * 10;
            drawHeart(context, heartX, heartY, true);
        }
    }

    private static void drawHeart(DrawContext context, int x, int y, boolean half) {
        RenderSystem.enableBlend();
        Identifier fullTexture = Identifier.ofVanilla("hud/heart/frozen_" + (half ? "half" : "full"));
        context.drawGuiTexture(fullTexture, x, y, SPRITE_SIZE, SPRITE_SIZE);
        RenderSystem.disableBlend();
    }
}
