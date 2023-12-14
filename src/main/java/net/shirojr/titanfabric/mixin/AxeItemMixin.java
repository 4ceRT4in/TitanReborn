package net.shirojr.titanfabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.item.AxeItem;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @ModifyArg(method = "<init>", at = @At("INVOKE"), index = 0)
    private static float initMixin(float original) {
        return 2.0f;
    }

}
