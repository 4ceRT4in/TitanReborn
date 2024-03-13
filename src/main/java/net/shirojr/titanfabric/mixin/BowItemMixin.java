package net.shirojr.titanfabric.mixin;

import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.items.SelectableArrows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(BowItem.class)
public class BowItemMixin {
    @Inject(method = "getProjectiles", at = @At("HEAD"),cancellable = true)
    private void titanfabric$getProjectiles(CallbackInfoReturnable<Predicate<ItemStack>> cir) {
        BowItem bowItem = (BowItem)(Object) this;
        if (!(bowItem instanceof SelectableArrows weaponWithSelectableArrows)) return;
        Predicate<ItemStack> validArrowItem = stack -> {
            for (Item arrow : weaponWithSelectableArrows.supportedArrows()) {
                if (stack.getItem().equals(arrow)) return true;
            }
            return BowItem.BOW_PROJECTILES.test(stack);
        };
        cir.setReturnValue(validArrowItem);
    }
}
