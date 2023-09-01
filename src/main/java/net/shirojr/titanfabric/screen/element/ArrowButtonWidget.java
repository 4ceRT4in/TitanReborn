package net.shirojr.titanfabric.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.TitanFabric;

import java.awt.*;

public class ArrowButtonWidget extends PressableWidget {
    private final PressAction onPress;
    public static final Identifier WIDGETS_TEXTURE = new Identifier(TitanFabric.MODID, "textures/gui/button_sprite.png");
    public static final Point TEXTURE_SIZE = new Point(400, 400);

    public ArrowButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);

        int xShift = getXImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        this.drawTexture(matrices, this.x, this.y, xShift * 20, 0, this.width / 2, this.height / 2);
        this.drawTexture(matrices, this.x + this.width / 2, this.y, TEXTURE_SIZE.x - this.width / 2, 46 + xShift * 20, this.width / 2, this.height / 2);

        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int hexColor = this.active ? 0xFFFFFF : 0xA0A0A0;
        ClickableWidget.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, hexColor | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public interface PressAction {
        void onPress(ArrowButtonWidget arrowButton);
    }

    private int getXImage(boolean hovered) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (hovered) {
            i = 2;
        }
        return i;
    }
}
