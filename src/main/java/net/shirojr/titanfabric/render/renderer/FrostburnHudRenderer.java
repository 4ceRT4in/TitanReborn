package net.shirojr.titanfabric.render.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;

public class FrostburnHudRenderer implements HudRenderCallback {
    public static final int SPRITE_SIZE = 9;
    public static final int SPACE_BETWEEN_SPRITES = 0;
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

        int startX = centerX - 11;
        int startY = bottomY - 39;

        int frostburnHearts = (int) Math.floor(frostburn / 2);
        boolean hasHalf = (frostburn % 2.0f) != 0f;

        for (int i = 0; i < frostburnHearts; i++) {
            int heartX = startX - (SPRITE_SIZE + SPACE_BETWEEN_SPRITES) * i;
            int row = i % 10;
            int heartY = startY - (10 * row);    // assumes 10px between rows

            drawHeart(context, heartX, heartY, false);
        }

        if (hasHalf) {
            int heartX = startX + ((frostburnHearts + 1) % 10)/* * SPACE_BETWEEN_SPRITES*/;
            int heartY = startY - ((frostburnHearts + 1) / 10) * 10;
            drawHeart(context, heartX, heartY, true);
        }
    }

    private static void drawHeart(DrawContext context, int x, int y, boolean half) {
        Identifier textureIdentifier = Identifier.ofVanilla("hud/heart/frozen_" + (half ? "half" : "full"));
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        if (half) {
            matrices.translate(x - SPRITE_SIZE * 0.5f, y - SPRITE_SIZE * 0.5f, 0);
            matrices.scale(-1, 1, 1);
            matrices.translate( x + (-SPRITE_SIZE * 0.5f), y + (-SPRITE_SIZE * 0.5f), 0);
        }
        context.drawGuiTexture(textureIdentifier, x, y, SPRITE_SIZE, SPRITE_SIZE);
        matrices.pop();
    }
}
