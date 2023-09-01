package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.shirojr.titanfabric.item.TitanFabricItems;

import java.util.List;

public class ArrowSelectionScreen extends Screen {
    private final List<ItemStack> itemStacks;
    private static final int BUTTON_WIDTH = 45, BUTTON_HEIGHT = 45;
    private static final int BUTTON_X = 20, BUTTON_Y = 20;
    private static final int BUTTON_INNER_MARGIN = 0;

    public ArrowSelectionScreen(Text title, List<ItemStack> itemStacks) {
        super(title);
        this.itemStacks = itemStacks;
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new ButtonWidget(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableText(""), button -> {
            if (client == null) return;
            this.client.inGameHud.getChatHud().queueMessage(new LiteralText("Pressed Button in GUI"));
        }
        ));

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        int itemStackScale = 16;
        int itemStackX = BUTTON_X + (BUTTON_WIDTH / 2 - itemStackScale / 2);
        int itemStackY = BUTTON_Y + (BUTTON_HEIGHT / 2 - itemStackScale / 2);

        this.itemRenderer.renderInGui(TitanFabricItems.LEGEND_INGOT.getDefaultStack(), itemStackX, itemStackY);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, TitanFabricItems.LEGEND_INGOT.getDefaultStack(), itemStackX, itemStackY);
        this.itemRenderer.zOffset = 0.0f;
    }
}
