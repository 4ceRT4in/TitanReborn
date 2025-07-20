package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

public class ExtendedInventoryScreen extends HandledScreen<ExtendedInventoryScreenHandler> {
    public static final Identifier TEXTURE = TitanFabric.getId("textures/gui/extended_inventory.png");

    public ExtendedInventoryScreen(ExtendedInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title == null ? Text.of("") : title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 86;
        this.titleY = 6;
        this.playerInventoryTitleX = this.titleX;

        if (client == null || this.client.player == null || this.client.interactionManager == null) return;
        if (this.client.interactionManager.hasCreativeInventory()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()));
            return;
        }
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.literal("<<"), button -> {
            if (this.client.mouse != null) {
                this.client.mouse.unlockCursor();
            }
            this.client.setScreen(new InventoryScreen(this.client.player));
        }).dimensions(this.x + 2, this.height / 2 - 106, 20, 20).build();
        this.addDrawableChild(buttonWidget);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        if (this.client == null || this.client.player == null) return;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        InventoryScreen.drawEntity(context, x + 26, y + 8, x + 75, y + 78, 30, 0.0625F, mouseX, mouseY, this.client.player);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
        context.drawText(this.textRenderer, Text.translatable("screen.titanfabric.extended_inventory.title"),
                this.titleX, this.titleY, 0x404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context, delta, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}

