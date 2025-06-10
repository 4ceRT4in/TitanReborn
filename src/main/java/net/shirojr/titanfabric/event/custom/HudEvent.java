package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.SelectableArrows;

public class HudEvent {
    public static void register() {
        HudRenderCallback.EVENT.register(HudEvent::renderArrowSelection);
    }

    private static void renderArrowSelection(DrawContext context, RenderTickCounter tickCounter) {
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
            y -= 25;
        }
        context.drawItem(selectedArrowStack, x, y);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, selectedArrowStack, x, y);
    }
}
