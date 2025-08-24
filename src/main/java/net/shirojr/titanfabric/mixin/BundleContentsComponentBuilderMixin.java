package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContentsComponent.Builder.class)
public abstract class BundleContentsComponentBuilderMixin implements TooltipData {

    @WrapOperation(method = "addInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemsAndComponentsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean addInternal(ItemStack existing, ItemStack incoming, Operation<Boolean> op) {
        return op.call(existing, incoming) && existing.getCount() < existing.getMaxCount();
    }
}