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
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;
import net.shirojr.titanfabric.render.model.ParachuteFeatureModel;

public class ParachuteFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier PARACHUTE_TEXTURE = new Identifier(TitanFabric.MODID, "textures/entity/parachute/parachute.png");
    public static ParachuteFeatureModel parachuteModel;

    public ParachuteFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        parachuteModel = new ParachuteFeatureModel(ParachuteFeatureModel.getModelData().createModel());
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch) {
        if (!TitanFabricParachuteItem.isParachuteActivated(entity)) {
            return;
        }
        matrices.push();
        matrices.scale(4, 4, 4);
        matrices.translate(0, -1.36, 0);
        parachuteModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(PARACHUTE_TEXTURE)), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();
    }

}
