package net.shirojr.titanfabric.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;

public class ParachuteFeatureModel extends SinglePartEntityModel {
    private final ModelPart root;
    private final ModelPart glider;
    private final ModelPart strings;

    public ParachuteFeatureModel(ModelPart root) {
        this.root = root;
        this.glider = root.getChild("glider");
        this.strings = root.getChild("strings");
        getModelData();
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    public static TexturedModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData glider = modelPartData.addChild("glider", ModelPartBuilder.create()
                        .uv(0, 9)
                        .cuboid(-0.3523F, 0.2288F, -2.0F, 4.7046F, 0.05F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, 10.0F, -2.0F));

        ModelPartData glider_top_right_r1 = glider.addChild("glider_top_right_r1", ModelPartBuilder.create()
                .uv(2, 9).mirrored()
                .cuboid(0.0F, -0.125F, -4.0F, 4.0F, 0.05F, 8.0F, new Dilation(0.0F))
                .mirrored(false), ModelTransform.of(-4.0F, 1.875F, 2.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData glider_top_left_r1 = glider.addChild("glider_top_left_r1", ModelPartBuilder.create()
                        .uv(3, 9).cuboid(-4.0F, -0.125F, -4.0F, 4.0F, 0.05F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(8.0F, 1.875F, 2.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData strings = modelPartData.addChild("strings", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        ModelPartData strings_back_r1 = strings.addChild("strings_back_r1", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.5F, -0.3927F, 0.0F, 0.0F));

        ModelPartData strings_front_r1 = strings.addChild("strings_front_r1", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, -0.5F, 0.3927F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}
