package net.shirojr.titanfabric.render.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;
import net.shirojr.titanfabric.render.model.ParachuteFeatureModel;

import java.util.HashMap;
import java.util.Map;

public class ParachuteFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    private static final Map<String, Identifier> PARACHUTE_TEXTURES = new HashMap<>();

    static {
        for (String color : TitanFabricDyeProviders.COLOR_KEYS) {
            PARACHUTE_TEXTURES.put(color, TitanFabric.getId("textures/entity/parachute/parachute_" + color + ".png"));
        }
    }

    public static ParachuteFeatureModel parachuteModel;

    public ParachuteFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        parachuteModel = new ParachuteFeatureModel(ParachuteFeatureModel.getModelData().createModel());
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                       float headYaw, float headPitch) {
        if (!TitanFabricParachuteItem.isParachuteActivated(entity)) {
            return;
        }
        String color = getParachuteColor(entity);
        Identifier texture = PARACHUTE_TEXTURES.getOrDefault(color, PARACHUTE_TEXTURES.get("white"));

        matrices.push();
        matrices.scale(4, 4, 4);
        matrices.translate(0, -1.36, 0);
        parachuteModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture)), light, OverlayTexture.DEFAULT_UV, ColorHelper.Argb.fromFloats(1f, 1f, 1f, 1f));
        matrices.pop();
    }

    private String getParachuteColor(T entity) {
        ItemStack mainHand = entity.getMainHandStack();
        if (mainHand.isOf(TitanFabricItems.PARACHUTE) && mainHand.hasNbt()) {
            NbtCompound nbt = mainHand.getNbt();
            assert nbt != null;
            if (nbt.contains("Activated") && nbt.getBoolean("Activated")) {
                return TitanFabricDyeProviders.getColorFromNBT(nbt);
            }
        }
        ItemStack offHand = entity.getOffHandStack();
        if (offHand.isOf(TitanFabricItems.PARACHUTE) && offHand.hasNbt()) {
            NbtCompound nbt = offHand.getNbt();
            assert nbt != null;
            if (nbt.contains("Activated") && nbt.getBoolean("Activated")) {
                return TitanFabricDyeProviders.getColorFromNBT(nbt);
            }
        }
        return "white";
    }
}