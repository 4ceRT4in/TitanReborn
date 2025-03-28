package net.shirojr.titanfabric.screen.custom;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.network.NetworkingIdentifiers;
import net.shirojr.titanfabric.screen.element.ArrowButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class ArrowSelectionScreen extends Screen {
    private final List<ItemStack> itemStacks;
    private final List<ArrowButtonWidget> arrowButtonWidgets = new ArrayList<>();

    public ArrowSelectionScreen(Text title, List<ItemStack> itemStacks) {
        super(title);
        this.itemStacks = itemStacks;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);

            ArrowButtonWidget widget = new ArrowButtonWidget(i, this.width / 2, this.height / 2, 35, 35, null,
                    arrowButtonWidget -> {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeItemStack(itemStack);
                        ClientPlayNetworking.send(NetworkingIdentifiers.BOW_SCREEN_CHANNEL, buf);
                    }, itemRenderer);
            arrowButtonWidgets.add(widget);
            this.addDrawableChild(widget);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        for (ArrowButtonWidget widget : arrowButtonWidgets) {
            widget.renderButton(matrices, mouseX, mouseY, delta);
        }
    }
}
