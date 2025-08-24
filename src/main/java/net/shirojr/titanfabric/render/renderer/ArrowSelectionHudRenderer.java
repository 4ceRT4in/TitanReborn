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
        SelectableArrow selectionHandler;
        ItemStack weaponStack;
        if (player.getMainHandStack().getItem() instanceof SelectableArrow selectableArrow) {
            selectionHandler = selectableArrow;
            weaponStack = player.getMainHandStack();
        } else if (player.getOffHandStack().getItem() instanceof SelectableArrow selectableArrow) {
            selectionHandler = selectableArrow;
            weaponStack = player.getOffHandStack();
        } else {
            return;
        }
        Integer selectedIndex = selectionHandler.getSelectedIndex(weaponStack);
        if (selectedIndex == null) return;
        ItemStack selectedArrowStack = player.getInventory().getStack(selectedIndex);
        int x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 118;
        int y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 19;
        if (!player.getOffHandStack().isEmpty()) {
            y -= 25;
        }
        drawContext.drawItem(selectedArrowStack, x, y);
        drawContext.drawItemInSlot(MinecraftClient.getInstance().textRenderer, selectedArrowStack, x, y);
    }
}
