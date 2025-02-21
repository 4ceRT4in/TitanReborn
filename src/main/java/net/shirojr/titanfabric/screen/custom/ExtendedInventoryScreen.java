package net.shirojr.titanfabric.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

import java.util.ArrayList;
import java.util.List;

public class ExtendedInventoryScreen extends HandledScreen<ExtendedInventoryScreenHandler> {
    public static final Identifier TEXTURE = new Identifier(TitanFabric.MODID, "textures/gui/extended_inventory.png");

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
            this.client.setScreen(new CreativeInventoryScreen(this.client.player));
            return;
        }
        ButtonWidget buttonWidget = new ButtonWidget(this.x + 2, this.height / 2 - 106,
                20, 20, new LiteralText("<<"), button -> {
            if(this.client.mouse != null) {
                this.client.mouse.unlockCursor();
            }
            this.client.setScreen(new InventoryScreen(this.client.player));
        });
        this.addDrawableChild(buttonWidget);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (this.client == null || this.client.player == null) return;
        InventoryScreen.drawEntity(x + 51, y + 75, 30,
                (float) (x + 51) - mouseX, (float) (y + 75 - 50) - mouseY,
                this.client.player);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        int color = 0x404040;

        this.textRenderer.draw(matrices, new TranslatableText("screen.titanfabric.extended_inventory.title"), this.titleX, this.titleY, color);
/*        for (int line = 0; line < this.handler.getDescription().size(); line++) {
            int yOffset = 52 + (line * 10);
            Text lineText = new LiteralText(this.handler.getDescription().get(line));
            this.textRenderer.draw(matrices, lineText, this.titleX, this.titleY + yOffset, color);
        }*/
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}

