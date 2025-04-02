package net.shirojr.titanfabric.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.items.Anvilable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class ItemMixin {
    @Redirect(method = "isEnchantable", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z"))
    private boolean titanfabric$enchantmentProductIsEnchantable(ItemStack instance, ComponentType<Integer> componentType) {
        return instance.contains(DataComponentTypes.MAX_DAMAGE) || instance.getItem() instanceof Anvilable;
    }
}
