package net.shirojr.titanfabric.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ArrowButtonWidget extends PressableWidget {
    private final int index;
    private final PressAction onPress;
    private final ItemRenderer itemRenderer;
    public static final Identifier WIDGETS_TEXTURE = new Identifier(TitanFabric.MOD_ID, "textures/gui/button_sprite_small.png");

    public ArrowButtonWidget(int index, int x, int y, int width, int height,
                             @Nullable Text message, PressAction onPress, ItemRenderer itemRenderer) {
        super(x, y, width, height, message == null ? new LiteralText("") : message);
        this.index = index;
        this.onPress = onPress;
        this.itemRenderer = itemRenderer;
    }

    public static Optional<ArrowButtonWidget> getWidgetFromList(int index, List<ArrowButtonWidget> list) {
        for (ArrowButtonWidget widget : list) {
            if (widget.getIndex() == index) return Optional.of(widget);
        }
        return Optional.empty();
    }

    public int getIndex() {
        return this.index;
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

        renderWidget(matrices);
        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
    }

    private void renderWidget(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexture(matrices, this.x, this.y, isHovered() ? 35 : 0, 0, this.width - 13, this.height - 13);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public interface PressAction {
        void onPress(ArrowButtonWidget arrowButton);
    }
}
