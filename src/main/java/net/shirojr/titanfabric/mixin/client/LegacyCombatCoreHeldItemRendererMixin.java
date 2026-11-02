package net.shirojr.titanfabric.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Applied after LegacyCombatCore so this HEAD callback precedes its bow-pose replacement. */
@Mixin(value = HeldItemRenderer.class, priority = 900)
public abstract class LegacyCombatCoreHeldItemRendererMixin {
    @Shadow
    public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode,
                                    boolean leftHanded, MatrixStack matrices,
                                    VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void titanfabric$renderVanillaOffhandBow(AbstractClientPlayerEntity player, float tickDelta,
                                                   float pitch, Hand hand, float swingProgress, ItemStack item,
                                                   float equipProgress, MatrixStack matrices,
                                                   VertexConsumerProvider vertexConsumers, int light,
                                                   CallbackInfo ci) {
        if (hand != Hand.OFF_HAND || player.isUsingSpyglass() || !player.isUsingItem()
                || player.getItemUseTimeLeft() <= 0 || player.getActiveHand() != hand
                || item.isEmpty() || item.getUseAction() != UseAction.BOW) {
            return;
        }

        // LegacyCombatCore 1.0.0 cancels vanilla rendering and uses legacy rotations with
        // ModelTransformationMode.NONE. Its offhand pose bypasses the bow model's hand
        // transforms. Use the vanilla 1.21.1 BOW branch here, including the model context.
        Arm arm = player.getMainArm().getOpposite();
        int direction = arm == Arm.RIGHT ? 1 : -1;
        matrices.push();
        matrices.translate(direction * 0.56F, -0.52F - equipProgress * 0.6F, -0.72F);
        matrices.translate(direction * -0.2785682F, 0.18344387F, 0.15731531F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(direction * 35.3F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(direction * -9.785F));

        float useTicks = item.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
        float pull = useTicks / 20.0F;
        pull = Math.min((pull * pull + pull * 2.0F) / 3.0F, 1.0F);
        if (pull > 0.1F) {
            float shake = MathHelper.sin((useTicks - 0.1F) * 1.3F) * (pull - 0.1F);
            matrices.translate(0.0F, shake * 0.004F, 0.0F);
        }
        matrices.translate(0.0F, 0.0F, pull * 0.04F);
        matrices.scale(1.0F, 1.0F, 1.0F + pull * 0.2F);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(direction * 45.0F));
        this.renderItem(player, item,
                arm == Arm.RIGHT ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
                arm == Arm.LEFT, matrices, vertexConsumers, light);
        matrices.pop();
        ci.cancel();
    }
}
