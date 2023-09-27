package net.shirojr.titanfabric.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)

public class TitanFabricParachuteFeatureRenderer <T extends LivingEntity, M extends EntityModel<T>>
        extends FeatureRenderer<T, M> {
    // private final TitanFabricParachuteFeatureModel<T> model;
    public static final EntityModelLayer MODEL_PARACHUTE_LAYER = new EntityModelLayer(new Identifier("entitytesting", "cube"), "main");

    public TitanFabricParachuteFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

    }
}
