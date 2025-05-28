package net.shirojr.titanfabric.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ImageAnimation {
    public MinecraftClient mc;

    public Identifier img;

    public long startTime;

    public long fadeInTime = 700L;

    public long stayTime = 3000L;

    public long fadeOutTime = 1000L;

    public float scale = 1F;

    public boolean done = false;

    public ImageAnimation(MinecraftClient mc, Identifier img, long startTime, long fadeInTime, long stayTime, long fadeOutTime, float scale) {
        this.mc = mc;
        this.img = img;
        this.startTime = startTime;
        this.fadeInTime = fadeInTime;
        this.stayTime = stayTime;
        this.fadeOutTime = fadeOutTime;
        this.scale = scale;
    }

    public ImageAnimation(MinecraftClient mc, Identifier img, long startTime) {
        this.mc = mc;
        this.img = img;
        this.startTime = startTime;
    }

    public void render(MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.img);

        long time = System.currentTimeMillis();
        int width = this.mc.getWindow().getScaledWidth();
        int height = this.mc.getWindow().getScaledHeight();

        float imgWidth = 150.0F * this.scale;

        if (time - this.startTime < this.fadeInTime) {
            float perc = (float)(time - this.startTime) / (float)this.fadeInTime;
            float scale = (1.0F - Math.min(1.0F, perc)) * 4.0F + 1.0F;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.min(1.0F, perc));
            drawTexturedModalRect(matrices,
                    (int)(width / 2.0D - (imgWidth * scale / 2.0F)),
                    (int)(height / 2.0D - (imgWidth * scale / 2.0F)),
                    0, 0, 256, 256, (int)(imgWidth * scale), (int)(imgWidth * scale));
        } else if (time - this.startTime < this.fadeInTime + this.stayTime) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(matrices,
                    (int)(width / 2.0D - (imgWidth / 2.0F)),
                    (int)(height / 2.0D - (imgWidth / 2.0F)),
                    0, 0, 256, 256, (int)imgWidth, (int) imgWidth);
        } else if (time - this.startTime < this.fadeInTime + this.stayTime + this.fadeOutTime) {
            float perc = (float)(time - this.startTime - this.fadeInTime - this.stayTime) / (float)this.fadeOutTime;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - Math.min(1.0F, perc));
            drawTexturedModalRect(matrices,
                    (int)(width / 2.0D - (imgWidth / 2.0F)),
                    (int)(height / 2.0D - (imgWidth / 2.0F)),
                    0, 0, 256, 256, (int)imgWidth, (int) imgWidth);
        } else {
            this.done = true;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
    public void drawTexturedModalRect(MatrixStack matrices, int x, int y, int u, int v, int texWidth, int texHeight, int width, int height) {
        float uScale = 1.0F / 256.0F;
        float vScale = 1.0F / 256.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrices.peek().getPositionMatrix(), x, y + height, 0)
                .texture(u * uScale, (v + texHeight) * vScale).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), x + width, y + height, 0)
                .texture((u + texWidth) * uScale, (v + texHeight) * vScale).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), x + width, y, 0)
                .texture((u + texWidth) * uScale, v * vScale).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), x, y, 0)
                .texture(u * uScale, v * vScale).next();

        tessellator.draw();
    }
}