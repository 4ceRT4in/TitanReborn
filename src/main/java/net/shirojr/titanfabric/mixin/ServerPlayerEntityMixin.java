package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.item.custom.spear.TitanFabricSpearItem;
import net.shirojr.titanfabric.item.custom.misc.ParachuteItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void titanfabric$preventDroppingThrownSpear(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (TitanFabricSpearItem.isThrowLocked(player, player.getMainHandStack())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onHandledScreenClosed", at = @At("HEAD"))
    private void titanfabric$returnLockedSpearFromCursor(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ItemStack cursor = player.currentScreenHandler.getCursorStack();
        if (!TitanFabricSpearItem.isThrowLocked(player, cursor)) return;

        int emptySlot = player.getInventory().getEmptySlot();
        if (emptySlot == -1) return;
        player.getInventory().setStack(emptySlot, cursor);
        player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void titanfabric$onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ImmunityEffect.resetImmunity(player);
    }


    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(ParachuteItem.isParachuteActivated(player)) {
            if (source.isIn(DamageTypeTags.IS_FALL) || source.isOf(DamageTypes.FALL)) {
                cir.setReturnValue(false);
            }
        }
    }
}
