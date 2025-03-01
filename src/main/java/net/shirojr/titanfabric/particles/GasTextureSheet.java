package net.shirojr.titanfabric.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import org.jetbrains.annotations.Nullable;

public class GasTextureSheet {

    public static final Identifier GAS_FOG_TEXTURE = TitanFabric.getId("textures/particle/gas_particle.png");


    public static ParticleTextureSheet Gas = new ParticleTextureSheet() {
        @Nullable
        @Override
        public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SrcFactor.SRC_ALPHA,
                    GlStateManager.DstFactor.ONE,
                    GlStateManager.SrcFactor.ONE,
                    GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
            );
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        }

        @Override
        public String toString() {
            return "GAS_PARTICLE";
        }
    };

    public static void initialize() {
    }
}
