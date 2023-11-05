package net.shirojr.titanfabric.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.shirojr.titanfabric.item.TitanFabricItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerHeldItemFeatureRenderer.class)
public class PlayerHeldItemFeatureRendererMixin {
    @Inject(method = "renderItem", at = @At(value = "HEAD"), cancellable = true)
    private void titanfabric$avoidParachuteItemRenderer(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm,
                                                        MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (stack.isOf(TitanFabricItems.PARACHUTE)) {
            ci.cancel();
        }
    }
}
