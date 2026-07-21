package net.shirojr.titanfabric.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Allows unbreakable, unenchanted weapons with additional effects into either grindstone input slot. */
@Mixin(targets = {
        "net.minecraft.screen.GrindstoneScreenHandler$2",
        "net.minecraft.screen.GrindstoneScreenHandler$3"
})
public abstract class GrindstoneInputSlotMixin extends Slot {
    protected GrindstoneInputSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    private void titanfabric$allowAdditionalEffects(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && EffectHelper.hasAdditionalWeaponEffects(stack)) {
            cir.setReturnValue(true);
        }
    }
}
