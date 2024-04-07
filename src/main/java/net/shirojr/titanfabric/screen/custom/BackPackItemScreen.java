package net.shirojr.titanfabric.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;

public class BackPackItemScreen extends HandledScreen<BackPackItemScreenHandler> {
    private static final Identifier TEXTURE_SMALL = new Identifier(TitanFabric.MODID, "textures/gui/backpack_small.png");
    private static final Identifier TEXTURE_MEDIUM = new Identifier(TitanFabric.MODID, "textures/gui/backpack_medium.png");
    private static final Identifier TEXTURE_BIG = new Identifier(TitanFabric.MODID, "textures/gui/backpack_big.png");
    public BackPackItemScreen(BackPackItemScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // adjust title position here using titleX, if needed
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        switch (handler.getBackPackItemType()) {
            case BIG -> RenderSystem.setShaderTexture(0, TEXTURE_BIG);
            case MEDIUM -> RenderSystem.setShaderTexture(0, TEXTURE_MEDIUM);
            default -> RenderSystem.setShaderTexture(0, TEXTURE_SMALL);
        }

        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
