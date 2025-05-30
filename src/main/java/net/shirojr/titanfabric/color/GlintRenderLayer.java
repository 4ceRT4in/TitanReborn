package net.shirojr.titanfabric.color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

// This class lovingly yoinked (& updated to 1.18) from
// https://github.com/VazkiiMods/Quark/blob/master/src/main/java/vazkii/quark/content/tools/client/GlintRenderType.java
// Published under the "CC BY-NC-SA 3.0" Creative Commons License
@Environment(EnvType.CLIENT)
public class GlintRenderLayer extends RenderLayer {

    public static List<RenderLayer> glintColor = newRenderList(GlintRenderLayer::buildGlintRenderLayer);
    public static List<RenderLayer> entityGlintColor = newRenderList(GlintRenderLayer::buildEntityGlintRenderLayer);
    public static List<RenderLayer> glintDirectColor = newRenderList(GlintRenderLayer::buildGlintDirectRenderLayer);
    public static List<RenderLayer> entityGlintDirectColor = newRenderList(GlintRenderLayer::buildEntityGlintDirectRenderLayer);

    public static List<RenderLayer> armorGlintColor = newRenderList(GlintRenderLayer::buildArmorGlintRenderLayer);
    public static List<RenderLayer> armorEntityGlintColor = newRenderList(GlintRenderLayer::buildArmorEntityGlintRenderLayer);

    public static List<RenderLayer> translucentGlintColor = newRenderList(GlintRenderLayer::buildTranslucentGlint);

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> map) {
        addGlintTypes(map, glintColor);
        addGlintTypes(map, entityGlintColor);
        addGlintTypes(map, glintDirectColor);
        addGlintTypes(map, entityGlintDirectColor);
        addGlintTypes(map, armorGlintColor);
        addGlintTypes(map, armorEntityGlintColor);
        addGlintTypes(map, translucentGlintColor);
    }

    public GlintRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static List<RenderLayer> newRenderList(Function<String, RenderLayer> func) {
        ArrayList<RenderLayer> list = new ArrayList<>(DyeColor.values().length);

        for (DyeColor color : DyeColor.values())
            list.add(func.apply(color.getName()));

        return list;
    }

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> map, List<RenderLayer> typeList) {
        for(RenderLayer renderType : typeList)
            if (!map.containsKey(renderType))
                map.put(renderType, new BufferBuilder(renderType.getExpectedBufferSize()));
    }

    private static RenderLayer buildGlintRenderLayer(String name) {
        final Identifier res = new Identifier(TitanFabric.MODID, "textures/glints/glint_" + name + ".png");

        return RenderLayer.of("glint_" + name, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .shader(RenderPhase.GLINT_SHADER)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .build(false));
    }

    private static RenderLayer buildEntityGlintRenderLayer(String name) {
        final Identifier res = new Identifier(TitanFabric.MODID, "textures/glints/glint_" + name + ".png");

        return RenderLayer.of("entity_glint_" + name, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .shader(RenderPhase.ENTITY_GLINT_SHADER)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .target(ITEM_TARGET)
                .texturing(ENTITY_GLINT_TEXTURING)
                .build(false));
    }


    private static RenderLayer buildGlintDirectRenderLayer(String name) {
        final Identifier res = new Identifier(TitanFabric.MODID, "textures/glints/glint_" + name + ".png");

        return RenderLayer.of("glint_direct_" + name, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .shader(RenderPhase.DIRECT_GLINT_SHADER)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .build(false));
    }


    private static RenderLayer buildEntityGlintDirectRenderLayer(String name) {
        final Identifier res = new Identifier(TitanFabric.MODID, "textures/glints/glint_" + name + ".png");

        return RenderLayer.of("entity_glint_direct_" + name, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .shader(RenderPhase.DIRECT_ENTITY_GLINT_SHADER)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(ENTITY_GLINT_TEXTURING)
                .build(false));
    }

    private static RenderLayer buildArmorGlintRenderLayer(String name) {
        final Identifier res = new Identifier(TitanFabric.MODID, "textures/glints/glint_" + name + ".png");

        return RenderLayer.of("armor_glint_" + name, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .shader(RenderPhase.ARMOR_GLINT_SHADER)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(false));
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer(String name) {
        final Identifier res = new Identifier(TitanFabric.MODID, "textures/glints/glint_" + name + ".png");

        return RenderLayer.of("armor_entity_glint_" + name, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .shader(RenderPhase.ARMOR_ENTITY_GLINT_SHADER)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(ENTITY_GLINT_TEXTURING)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(false));
    }

    private static RenderLayer buildTranslucentGlint(String name) {
        final Identifier res = new Identifier(TitanFabric.MODID, "textures/glints/glint_" + name + ".png");

        return RenderLayer.of("glint_translucent_" + name, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
                .shader(TRANSLUCENT_GLINT_SHADER)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .target(ITEM_TARGET)
                .build(false));
    }
}