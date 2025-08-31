package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.util.DamageTiltAvoider;
import net.shirojr.titanfabric.util.handler.ArrowShootingHandler;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements DamageTiltAvoider {
    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract void setCurrentHand(Hand hand);

    @Unique
    ItemStack armorStackBuffer;

    @Override
    public @Nullable ItemStack titanReborn$getArmorStackBuffer() {
        return this.armorStackBuffer;
    }

    @Override
    public void titanReborn$setArmorStackBuffer(@Nullable ItemStack stack) {
        this.armorStackBuffer = stack;
    }

    @Inject(method = "updateHealth",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastDamageTaken:F",
                    opcode = Opcodes.PUTFIELD
            ),
            cancellable = true
    )
    private void avoidDamageTiltByArmorUnequip(float health, CallbackInfo ci) {
        ItemStack bufferStack = titanReborn$getArmorStackBuffer();
        if (bufferStack == null || !(bufferStack.getItem() instanceof LegendArmorItem)) return;

        titanReborn$setArmorStackBuffer(null);
        setHealth(health);

        ci.cancel();
    }

    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean addMultiBowUsageSlowness(ClientPlayerEntity player, Operation<Boolean> original) {
        boolean originalEvaluation = original.call(player);

        ItemStack multiBowStack = player.getMainHandStack();
        if (!(multiBowStack.getItem() instanceof MultiBowItem)) multiBowStack = player.getOffHandStack();
        if (!(multiBowStack.getItem() instanceof MultiBowItem)) return originalEvaluation;
        //TODO: nbt is not available on client side

        return originalEvaluation || ((ArrowShootingHandler) player).titanfabric$isShootingArrows();
    }
}
