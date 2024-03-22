package net.shirojr.titanfabric.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

public class ExtendedInventoryScreen extends HandledScreen<ExtendedInventoryScreenHandler> {
    private ButtonWidget buttonWidget;
    private String buttonText;
    private static final Identifier TEXTURE = new Identifier(TitanFabric.MODID, "textures/gui/extended_inventory.png");

    public ExtendedInventoryScreen(ExtendedInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.buttonText = buttonText;
    }

    @Override
    protected void init() {
        if (client == null || this.client.player == null || this.client.interactionManager == null) return;
        if (this.client.interactionManager.hasCreativeInventory()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player));
            return;
        }
        buttonWidget = new ButtonWidget(this.x, this.height / 2 - 104, 20, 20,
                new LiteralText(buttonText).formatted(Formatting.BOLD), button -> {
            if (buttonText.equals("O")) buttonText = "X";
            else buttonText = "O";
            buttonWidget.setMessage(new LiteralText(buttonText).formatted(Formatting.BOLD));
            // this.client.setScreen(new ExtendedInventoryScreen(client.player, buttonText));
        });
        this.addDrawableChild(buttonWidget);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }
}
