package net.shirojr.titanfabric.render.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;

public class FrostburnHudRenderer implements HudRenderCallback {
    public static final int SPRITE_SIZE = 9;

    private static FrostburnHudRenderer instance;

    private int yWobbleOffset;

    private FrostburnHudRenderer() {
        this.yWobbleOffset = 0;
    }

    public static FrostburnHudRenderer getInstance() {
        if (instance == null) {
            instance = new FrostburnHudRenderer();
        }
        return instance;
    }

    public static boolean isInstantiated() {
        return instance != null;
    }

    public int getYWobbleOffset() {
        return yWobbleOffset;
    }

    public void setYWobbleOffset(int yWobbleOffset) {
        this.yWobbleOffset = yWobbleOffset;
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        FrostburnComponent frostburnComponent = FrostburnComponent.get(player);
        float frostburn = frostburnComponent.getFrostburn();
        if (frostburn <= 0) return;

        int centerX = context.getScaledWindowWidth() / 2;
        int bottomY = context.getScaledWindowHeight();

        int startX = centerX - 19;
        int startY = bottomY - 39 + getYWobbleOffset();

        int frostburnHearts = (int) Math.floor(frostburn / 2);
        boolean hasHalf = (frostburn % 2.0f) != 0f;

        for (int i = 0; i < frostburnHearts; i++) {
            int heartX = startX - ((SPRITE_SIZE - 1) * i);
            int row = i / 10;
            int heartY = startY - (10 * row);    // assumes 10px between rows

            drawHeart(context, heartX, heartY, false);
        }

        if (hasHalf) {
            int row = frostburnHearts / 10;
            int heartX = startX - ((frostburnHearts) * (SPRITE_SIZE - 1));
            int heartY = startY - (10 * row);
            drawHeart(context, heartX, heartY, true);
        }
    }

    private static void drawHeart(DrawContext context, int x, int y, boolean half) {
        Identifier textureIdentifier = TitanFabric.getId("hud/heart/frozen_" + (half ? "half" : "full"));
        context.drawGuiTexture(textureIdentifier, x, y, 0, SPRITE_SIZE, SPRITE_SIZE);
    }
}
