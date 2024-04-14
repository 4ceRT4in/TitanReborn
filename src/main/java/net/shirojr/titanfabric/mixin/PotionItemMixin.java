package net.shirojr.titanfabric.mixin;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionItem.class)
public class PotionItemMixin {
    @Inject(method = "appendStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z"))
    private void titanfabric$avoidCustomPotionCreativeTabRegistration(ItemGroup group, DefaultedList<ItemStack> stacks, CallbackInfo ci) {

    }
}
