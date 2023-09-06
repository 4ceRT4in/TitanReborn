package net.shirojr.titanfabric.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.TitanFabric;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ArrowButtonWidget extends PressableWidget {
    private final PressAction onPress;
    private final ItemRenderer itemRenderer;
    public static final Identifier WIDGETS_TEXTURE = new Identifier(TitanFabric.MODID, "textures/gui/button_sprite.png");
    public static final Point TEXTURE_SIZE = new Point(200, 200);
    public final Point elementSize;

    public ArrowButtonWidget(int x, int y, int width, int height, @Nullable Text message, PressAction onPress, ItemRenderer itemRenderer) {
        super(x, y, width, height, message == null ? new LiteralText("") : message);
        this.onPress = onPress;
        this.itemRenderer = itemRenderer;
        this.elementSize = new Point(x, y);
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

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        /*this.drawTexture(matrices, this.x, this.y, xShift * 20, 0, this.width / 2, this.height / 2);
        this.drawTexture(matrices, this.x + this.width / 2, this.y, TEXTURE_SIZE.x - this.width / 2, 46 + xShift * 20, this.width / 2, this.height / 2);
*/
        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int hexColor = this.active ? 0xFFFFFF : 0xA0A0A0;
        ClickableWidget.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, hexColor | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);


        Point elementPos1 = new Point(0, 0);
        Point elementPos2 = new Point(elementSize.x / 2, 0);
        Point elementPos3 = new Point(0, elementSize.y / 2);
        Point elementPos4 = new Point(elementSize.x / 2, elementSize.y / 2);

        Point texturePos1 = getXShift(new Point(0, 0), this.isHovered());
        Point texturePos2 = getXShift(new Point(100, 0), this.isHovered());
        Point texturePos3 = getXShift(new Point(0, 100), this.isHovered());
        Point texturePos4 = getXShift(new Point(100, 100), this.isHovered());

        this.drawTexture(matrices, elementPos1.x, elementPos1.y, texturePos1.x, texturePos1.y, elementSize.x / 4, elementSize.y / 4);
        this.drawTexture(matrices, elementPos2.x, elementPos2.y, texturePos2.x, texturePos2.y, elementSize.x / 4, elementSize.y / 4);
        this.drawTexture(matrices, elementPos3.x, elementPos3.y, texturePos3.x, texturePos3.y, elementSize.x / 4, elementSize.y / 4);
        this.drawTexture(matrices, elementPos4.x, elementPos4.y, texturePos4.x, texturePos4.y, elementSize.x / 4, elementSize.y / 4);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public interface PressAction {
        void onPress(ArrowButtonWidget arrowButton);
    }

    private Point getXShift(Point input, boolean hovered) {
        int shift = 199;
        if (!isHovered()) return input;
        return new Point(input.x + shift, input.y);
    }
}
