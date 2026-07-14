package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.RotationAxis;
import net.shirojr.titanfabric.color.GlintContext;
import net.shirojr.titanfabric.color.GlintRenderLayer;
import net.shirojr.titanfabric.item.custom.spear.TitanFabricSpearItem;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
    private void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        DyeColor color = null;
        if(OverpoweredEnchantmentsHelper.isOverpowered(stack)) {
            color = DyeColor.ORANGE;
        }

        if (ArmorPlatingHelper.hasArmorPlating(stack)) {
            ArmorPlateType plateType = ArmorPlatingHelper.getArmorPlatingType(stack);
            if (plateType != null) {
                switch (plateType) {
                    case CITRIN -> color = DyeColor.YELLOW;
                    case LEGEND -> color = DyeColor.CYAN;
                    case NETHERITE -> color = DyeColor.BLACK;
                    case EMBER -> color = DyeColor.RED;
                    case DIAMOND -> color = DyeColor.BLUE;
                }
            }
        }

        if(OverpoweredEnchantmentsHelper.isOverpoweredEnchantmentBook(stack)) {
            color = DyeColor.ORANGE;
        }

        GlintContext.setColor(color);
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/json/Transformation;apply(ZLnet/minecraft/client/util/math/MatrixStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void titanfabric$orientHeldSpear(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!(stack.getItem() instanceof TitanFabricSpearItem)) return;
        if (renderMode != ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
                && renderMode != ModelTransformationMode.THIRD_PERSON_LEFT_HAND) return;

        // At this point the regular hand translation has already been applied.
        // Rotating here flips only the item around its own center, not the arm/hand.
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
    }

    @WrapOperation(method = "getArmorGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer wrapArmorEntityGlint(Operation<RenderLayer> original) {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.armorEntityGlintColor.get(color.getId());
        }
        return original.call();
    }

    @WrapOperation(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer wrapGetGlint(Operation<RenderLayer> original) {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.glintColor.get(color.getId());
        }
        return original.call();
    }

    @WrapOperation(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer wrapGetEntityGlint(Operation<RenderLayer> original) {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.entityGlintColor.get(color.getId());
        }
        return original.call();
    }

    @WrapOperation(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer wrapGetDirectGlint(Operation<RenderLayer> original) {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.glintDirectColor.get(color.getId());
        }
        return original.call();
    }

    @WrapOperation(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getDirectEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer wrapGetDirectEntityGlint(Operation<RenderLayer> original) {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.entityGlintDirectColor.get(color.getId());
        }
        return original.call();
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("RETURN"))
    private void afterRenderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        GlintContext.clear();
    }
}
