package net.shirojr.titanfabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.shirojr.titanfabric.access.GrindstoneScreenHandlerAccessor;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler implements GrindstoneScreenHandlerAccessor {
    @Unique
    private boolean titanfabric$platingAction;

    protected GrindstoneScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Override
    public boolean titanfabric$isPlatingAction() {
        return titanfabric$platingAction;
    }

    @Override
    public void titanfabric$setPlatingAction(boolean value) {
        titanfabric$platingAction = value;
    }

    @Inject(method = "getOutputStack", at = @At("HEAD"), cancellable = true)
    private void getOutputStack(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir) {
        titanfabric$platingAction = false;
        if (!firstInput.isEmpty() && secondInput.isEmpty() && firstInput.getCount() <= 1 && ArmorPlatingHelper.hasArmorPlating(firstInput)) {
            ItemStack out = firstInput.copy();
            ArmorPlatingHelper.removeAllArmorPlates(out);
            titanfabric$platingAction = true;
            cir.setReturnValue(out);
            return;
        }
        if (!secondInput.isEmpty() && firstInput.isEmpty() && secondInput.getCount() <= 1 && ArmorPlatingHelper.hasArmorPlating(secondInput)) {
            ItemStack out = secondInput.copy();
            ArmorPlatingHelper.removeAllArmorPlates(out);
            titanfabric$platingAction = true;
            cir.setReturnValue(out);
        }
    }
}