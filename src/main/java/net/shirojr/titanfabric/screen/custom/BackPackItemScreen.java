package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;

public class BackPackItemScreen extends HandledScreen<BackPackItemScreenHandler> {
    public BackPackItemScreen(BackPackItemScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // adjust title position here using titleX, if needed
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(this.handler.getBackPackItemType().getScreenTexture(), x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
