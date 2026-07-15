package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.shirojr.titanfabric.item.custom.spear.TitanFabricSpearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void titanfabric$lowerPulledSpear(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                              Hand hand, float swingProgress, ItemStack item, float equipProgress,
                                              MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                              CallbackInfo ci) {
        if (item.getItem() instanceof TitanFabricSpearItem && player.isUsingItem() && player.getActiveHand() == hand) {
            matrices.translate(0.0f, -0.25f, 0.0f);
        }
    }

    @WrapOperation(method = "renderFirstPersonItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderMapInOneHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFLnet/minecraft/util/Arm;FLnet/minecraft/item/ItemStack;)V"))
    )
    private boolean titanFabric$renderCrossBowFix(ItemStack instance, Item item, Operation<Boolean> original) {
        boolean originalEvaluation = original.call(instance, item);
        if (!item.equals(Items.CROSSBOW)) return originalEvaluation;
        return originalEvaluation || instance.getItem() instanceof CrossbowItem;
    }

    @WrapOperation(method = "getUsingItemHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean titanFabric$renderBowFix(ItemStack stack, Item item, Operation<Boolean> original) {
        boolean originalEvaluation = original.call(stack, item);

        if (originalEvaluation) return true;
        if (item == Items.BOW) {
            return stack.getItem() instanceof BowItem;
        }

        if (item == Items.CROSSBOW) {
            return stack.getItem() instanceof CrossbowItem;
        }
        return false;
    }

    @WrapOperation(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean titanFabric$renderBowFix2(ItemStack stack, Item item, Operation<Boolean> original) {
        boolean originalEvaluation = original.call(stack, item);
        if (originalEvaluation) return true;
        if (item == Items.BOW) return stack.getItem() instanceof BowItem;
        if (item == Items.CROSSBOW) return stack.getItem() instanceof CrossbowItem;
        return false;
    }
}
