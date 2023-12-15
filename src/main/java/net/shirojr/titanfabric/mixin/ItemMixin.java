package net.shirojr.titanfabric.mixin;

import net.minecraft.item.Item;
import net.shirojr.titanfabric.util.items.Anvilable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class ItemMixin {
    @Redirect(method = "isEnchantable", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isDamageable()Z"))
    private boolean titanfabric$enchantmentProductIsEnchantable(Item instance) {
        return instance.isDamageable() || instance instanceof Anvilable;
    }
}
