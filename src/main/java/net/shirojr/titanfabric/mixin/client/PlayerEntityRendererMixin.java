package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.shirojr.titanfabric.util.handler.ArrowShootingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @ModifyExpressionValue(method = "getArmPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean titanfabric$changeArmPoseForCrossbows(boolean original, @Local ItemStack stack) {
        return original || stack.getItem() instanceof CrossbowItem;
    }

    @Inject(method = "getArmPose", at = @At(value = "HEAD"), cancellable = true)
    private static void titanfabric$holdBowPoseWhenShooting(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (((ArrowShootingHandler)player).titanfabric$isShootingArrows()) {
            cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
