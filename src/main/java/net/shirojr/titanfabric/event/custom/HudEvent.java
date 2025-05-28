package net.shirojr.titanfabric.event.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.TitanFabricClient;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.SelectableArrows;

public class HudEvent {
    public static void register() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            int i;
            for (i = 0; i < TitanFabricClient.ANIMATIONS.size(); i++)
                (TitanFabricClient.ANIMATIONS.get(i)).render(matrixStack);
            for (i = 0; i < TitanFabricClient.ANIMATIONS.size(); i++) {
                if ((TitanFabricClient.ANIMATIONS.get(i)).done)
                    TitanFabricClient.ANIMATIONS.remove(i--);
            }

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null || player.isSpectator()) return;
            boolean isNotInMainHand = !(player.getMainHandStack().getItem() instanceof SelectableArrows);
            boolean isNotInOffHand = !(player.getOffHandStack().getItem() instanceof SelectableArrows);
            if (isNotInMainHand && isNotInOffHand) return;
            ArrowSelectionHandler arrowSelection = (ArrowSelectionHandler) player;
            if (arrowSelection.titanfabric$getSelectedArrowIndex().isEmpty()) return;
            ItemStack selectedArrowStack = player.getInventory().getStack(arrowSelection.titanfabric$getSelectedArrowIndex().get());
            int x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 118;
            int y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 19;
            if (!player.getOffHandStack().isEmpty()) {
                y = y - 25;
            }

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
