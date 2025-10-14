package net.shirojr.titanfabric.mixin;

import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public abstract class BundleContentsComponentMixin implements TooltipData {
    @Unique
    private static final Fraction BIG = Fraction.getFraction(1, 18);
    @Unique
    private static final Fraction MEDIUM = Fraction.getFraction(1, 12);
    @Unique
    private static final Fraction SMALL = Fraction.getFraction(1, 6);
    @Unique
    private static final Fraction POTION = Fraction.getFraction(1, 9);


    @Inject(method = "getOccupancy*", at = @At("RETURN"), cancellable = true)
    private static void getOccupancy(ItemStack stack, CallbackInfoReturnable<Fraction> cir) {
        cir.setReturnValue(cir.getReturnValue().multiplyBy(BIG));
    }
}
