package net.shirojr.titanfabric.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(net.minecraft.component.type.UnbreakableComponent.class)
public abstract class UnbreakableComponent implements TooltipAppender {

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void titanfabric$appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, CallbackInfo ci) {
        ci.cancel();
    }
}
