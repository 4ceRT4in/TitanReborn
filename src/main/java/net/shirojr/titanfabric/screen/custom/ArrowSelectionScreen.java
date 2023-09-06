package net.shirojr.titanfabric.screen.custom;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.network.TitanFabricNetworking;
import net.shirojr.titanfabric.screen.element.ArrowButtonWidget;

import java.util.List;

public class ArrowSelectionScreen extends Screen {
    private final List<ItemStack> itemStacks;
    private static final int BUTTONS_WIDTH = 45, BUTTONS_HEIGHT = 45, BUTTONS_MARGIN = 20;
    private int BUTTONS_X, BUTTONS_Y;

    public ArrowSelectionScreen(Text title, List<ItemStack> itemStacks) {
        super(title);
        this.itemStacks = itemStacks;

    }

    @Override
    protected void init() {
        super.init();

        this.BUTTONS_X = this.width / 2;
        this.BUTTONS_Y = this.height / 2 - 80;

        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);

            int elementX = BUTTONS_X + ((BUTTONS_WIDTH + BUTTONS_MARGIN) / itemStacks.size() * i);
            int elementY = BUTTONS_Y;

            this.addDrawableChild(new ArrowButtonWidget(elementX, elementY, BUTTONS_WIDTH, BUTTONS_HEIGHT, null,
                    arrowButtonWidget -> {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeItemStack(itemStack);
                        ClientPlayNetworking.send(TitanFabricNetworking.BOW_SCREEN_CHANNEL, buf);
                    }, itemRenderer));
        }

        //TODO: remove this element when done with testing
        this.addDrawableChild(new ButtonWidget(BUTTONS_X, BUTTONS_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT, new TranslatableText(""), button -> {
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
        int itemStackX = BUTTONS_X + (BUTTONS_WIDTH / 2 - itemStackScale / 2);
        int itemStackY = BUTTONS_Y + (BUTTONS_HEIGHT / 2 - itemStackScale / 2);

        this.itemRenderer.renderInGui(TitanFabricItems.LEGEND_INGOT.getDefaultStack(), itemStackX, itemStackY);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, TitanFabricItems.LEGEND_INGOT.getDefaultStack(), itemStackX, itemStackY);
        this.itemRenderer.zOffset = 0.0f;
    }
}
