package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
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
