package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
