package net.shirojr.titanfabric.render.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.cca.component.RecoveryBufferComponent;

public class RecoveryBufferHudRenderer implements HudRenderCallback {
    private static final Identifier HEART_CONTAINER_TEXTURE = Identifier.ofVanilla("hud/heart/container");
    private static final Identifier HEART_FULL_TEXTURE = Identifier.ofVanilla("hud/heart/full");
    private static final Identifier HEART_HALF_TEXTURE = Identifier.ofVanilla("hud/heart/half");
    private static final Identifier RECOVERY_FULL_TEXTURE = TitanFabric.getId("hud/heart/recovery_buffer_full");
    private static final Identifier RECOVERY_HALF_TEXTURE = TitanFabric.getId("hud/heart/recovery_buffer_half");
    private static final int HOTBAR_RIGHT_OFFSET = 94;
    private static final int HOTBAR_BOTTOM_OFFSET = 10;
    private static final int COLUMN_SPACING = 8;
    private static final int ROW_SPACING = 10;
    private static final int HEARTS_PER_ROW = 3;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isSpectator() || player.isCreative()) return;

        RecoveryBufferComponent component = RecoveryBufferComponent.get(player);
        float bufferAmount = component.getBufferAmount();
        if (component.getActiveProfile() == null) return;

        int centerX = context.getScaledWindowWidth() / 2;
        int bottomY = context.getScaledWindowHeight();
        int maxHearts = Math.max(1, (int) Math.ceil(component.getMaxBufferAmount() / 2.0f));
        int rows = (int) Math.ceil(maxHearts / (float) HEARTS_PER_ROW);
        int startX = centerX + HOTBAR_RIGHT_OFFSET;
        int startY = rows == 1
                ? bottomY - HOTBAR_BOTTOM_OFFSET - (ROW_SPACING / 2)
                : bottomY - HOTBAR_BOTTOM_OFFSET - ((rows - 1) * ROW_SPACING);

        int fullHearts = (int) Math.floor(bufferAmount / 2.0f);
        boolean halfHeart = bufferAmount % 2.0f != 0.0f;
        boolean healing = component.isHealingActive();
        float thawAlpha = healing
                ? 0.6f + (((MathHelper.sin(player.age * 0.45f) + 1.0f) / 2.0f) * 0.25f)
                : 1.0f;

        for (int i = 0; i < maxHearts; i++) {
            int x = getHeartX(startX, i);
            int y = getHeartY(startY, i, rows);
            context.drawGuiTexture(HEART_CONTAINER_TEXTURE, x, y, 0, 9, 9);
        }

        for (int i = 0; i < fullHearts; i++) {
            drawHeart(context, getHeartX(startX, i), getHeartY(startY, i, rows), false, thawAlpha);
        }

        if (halfHeart && fullHearts < maxHearts) {
            drawHeart(context, getHeartX(startX, fullHearts), getHeartY(startY, fullHearts, rows), true, thawAlpha);
        }
    }

    private static int getHeartX(int startX, int heartIndex) {
        return startX + ((heartIndex % HEARTS_PER_ROW) * COLUMN_SPACING);
    }

    private static int getHeartY(int startY, int heartIndex, int rows) {
        int fillRow = heartIndex / HEARTS_PER_ROW;
        int visualRow = rows - fillRow - 1;
        return startY + (visualRow * ROW_SPACING);
    }

    private static void drawHeart(DrawContext context, int x, int y, boolean half, float thawAlpha) {
        Identifier vanillaTexture = half ? HEART_HALF_TEXTURE : HEART_FULL_TEXTURE;
        Identifier recoveryTexture = half ? RECOVERY_HALF_TEXTURE : RECOVERY_FULL_TEXTURE;

        if (thawAlpha < 0.99f) {
            context.drawGuiTexture(vanillaTexture, x, y, 0, 9, 9);
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, thawAlpha);
        context.drawGuiTexture(recoveryTexture, x, y, 0, 9, 9);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}
