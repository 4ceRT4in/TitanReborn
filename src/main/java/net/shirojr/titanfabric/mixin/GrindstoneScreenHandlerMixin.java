package net.shirojr.titanfabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.shirojr.titanfabric.access.GrindstoneScreenHandlerAccessor;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
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
        if (firstInput.isOf(TitanFabricItems.ENCHANTED_DIAMOND_APPLE)
                || secondInput.isOf(TitanFabricItems.ENCHANTED_DIAMOND_APPLE)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        ItemStack output = titanfabric$getCustomOutput(firstInput, secondInput);
        if (!output.isEmpty()) {
            titanfabric$platingAction = true;
            cir.setReturnValue(output);
        }
    }

    @Unique
    private static ItemStack titanfabric$getCustomOutput(ItemStack firstInput, ItemStack secondInput) {
        ItemStack inputStack = ItemStack.EMPTY;
        if (!firstInput.isEmpty() && secondInput.isEmpty()) {
            inputStack = firstInput;
        } else if (!secondInput.isEmpty() && firstInput.isEmpty()) {
            inputStack = secondInput;
        }

        if (inputStack.isEmpty() || inputStack.getCount() > 1) {
            return ItemStack.EMPTY;
        }

        if (ArmorPlatingHelper.hasArmorPlating(inputStack)) {
            ItemStack out = inputStack.copy();
            ArmorPlatingHelper.removeAllArmorPlates(out);
            return out;
        }

        if (EffectHelper.hasAdditionalWeaponEffects(inputStack)) {
            return EffectHelper.createStackWithoutAdditionalEffects(inputStack);
        }

        return ItemStack.EMPTY;
    }
}
