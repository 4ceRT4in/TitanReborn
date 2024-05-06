package net.shirojr.titanfabric.event.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.SelectableArrows;

public class HudEvent {
    public static void register() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (!(player.getMainHandStack().getItem() instanceof SelectableArrows)) return;
            ArrowSelectionHandler arrowSelection = (ArrowSelectionHandler) player;
            if (arrowSelection.titanfabric$getSelectedArrow().isEmpty()) return;
            ItemStack selectedArrowStack = arrowSelection.titanfabric$getSelectedArrow().get();
            int x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 120;
            int y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 19;

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            itemRenderer.renderInGui(selectedArrowStack, x, y);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, selectedArrowStack, x, y);

            RenderSystem.disableBlend();
        });
    }
}