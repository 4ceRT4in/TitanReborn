package net.shirojr.titanfabric.render.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.misc.ParachuteItem;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.render.model.ParachuteFeatureModel;

import java.util.HashMap;
import java.util.Map;

public class ParachuteFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Map<String, Identifier> PARACHUTE_TEXTURES = new HashMap<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            PARACHUTE_TEXTURES.put(color.getName(), TitanFabric.getId("textures/entity/parachute/parachute_" + color + ".png"));
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
        if (!ParachuteItem.isParachuteActivated(entity)) {
            return;
        }
        String color = getParachuteColor(entity);
        Identifier texture = PARACHUTE_TEXTURES.getOrDefault(color, PARACHUTE_TEXTURES.get("white"));

        matrices.push();
        matrices.scale(4, 4, 4);
        matrices.translate(0, -1.36, 0);
        parachuteModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture)), light, OverlayTexture.DEFAULT_UV, -1);
        matrices.pop();
    }

    private String getParachuteColor(T entity) {
        ItemStack mainHand = entity.getMainHandStack();
        var comp = DataComponentTypes.BASE_COLOR;
        if (mainHand.isOf(TitanFabricItems.PARACHUTE) && mainHand.get(comp) != null) {
            if (Boolean.TRUE.equals(mainHand.get(TitanFabricDataComponents.ACTIVATED))) {
                return TitanFabricDyeProviders.getColorFromNBT(mainHand);
            }
        }
        ItemStack offHand = entity.getOffHandStack();
        if (offHand.isOf(TitanFabricItems.PARACHUTE) && offHand.get(comp) != null) {
            if (Boolean.TRUE.equals(mainHand.get(TitanFabricDataComponents.ACTIVATED))) {
                return TitanFabricDyeProviders.getColorFromNBT(offHand);
            }
        }
        return "white";
    }
}