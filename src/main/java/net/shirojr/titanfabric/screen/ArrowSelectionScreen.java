package net.shirojr.titanfabric.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.shirojr.titanfabric.network.TitanFabricNetworking;

public class ArrowSelectionScreen extends Screen {
    private final int slotIndex;

    /**
     *
     * @param title
     * @param slotIndex
     */
    public ArrowSelectionScreen(Text title, int slotIndex) {
        super(title);
        this.slotIndex = slotIndex;
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new ButtonWidget(20, 20, 20, 20, new TranslatableText("Press me harder"), button -> {
            var buff = PacketByteBufs.create();
            buff.writeByte(this.slotIndex);
            buff.writeByte(1);
            ClientPlayNetworking.send(TitanFabricNetworking.BOW_SCREEN_CHANNEL, buff);
        }
        ));
        this.addDrawableChild(new ButtonWidget(20, 20, 20, 20, new TranslatableText("Press me harder"), button -> {
            var buff = PacketByteBufs.create();
            buff.writeByte(this.slotIndex);
            buff.writeByte(2);
            ClientPlayNetworking.send(TitanFabricNetworking.BOW_SCREEN_CHANNEL, buff);
        }
        ));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }
}
