package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.handler.ArrowShootingHandler;
import net.shirojr.titanfabric.util.items.MultiBowHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean addMultiBowUsageSlowness(ClientPlayerEntity player, Operation<Boolean> original) {
        boolean originalEvaluation = original.call(player);

        ItemStack multiBowStack = player.getMainHandStack();
        if (!(multiBowStack.getItem() instanceof MultiBowItem)) multiBowStack = player.getOffHandStack();
        if (!(multiBowStack.getItem() instanceof MultiBowItem)) return originalEvaluation;
        //TODO: nbt is not available on client side

        return originalEvaluation || ((ArrowShootingHandler)player).titanfabric$isShootingArrows();
    }
}
