package net.shirojr.titanfabric.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class TitanFabricParachuteFeatureModel<T extends Entity>
        extends SinglePartEntityModel<T> {
    private final ModelPart base;

    public TitanFabricParachuteFeatureModel(ModelPart base) {
        this.base = base;
    }

    @Override
    public ModelPart getPart() {
        return this.base;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.base.pitch = 0;
        this.base.yaw = 0;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.translate(0, 1.125, 0);
        base.render(matrices, vertices, light, overlay);
    }
}
