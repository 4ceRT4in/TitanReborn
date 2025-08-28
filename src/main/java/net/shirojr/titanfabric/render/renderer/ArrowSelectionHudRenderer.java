package net.shirojr.titanfabric.render.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.items.SelectableArrow;

public class ArrowSelectionHudRenderer implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isSpectator()) return;
        ItemStack weaponStack;
        if (player.getMainHandStack().getItem() instanceof SelectableArrow) {
            weaponStack = player.getMainHandStack();
        } else if (player.getOffHandStack().getItem() instanceof SelectableArrow) {
            weaponStack = player.getOffHandStack();
        } else {
            return;
        }

        ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(weaponStack, player.getInventory().main);
        if (selectedArrowStack == null) return;
        int x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 118;
        int y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 19;
        if (!player.getOffHandStack().isEmpty()) {
            y -= 25;
        }
        drawContext.drawItem(selectedArrowStack, x, y);
        drawContext.drawItemInSlot(MinecraftClient.getInstance().textRenderer, selectedArrowStack, x, y);
    }
}
