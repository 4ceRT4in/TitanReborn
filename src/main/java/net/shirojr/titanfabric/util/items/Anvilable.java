package net.shirojr.titanfabric.util.items;

/**
 * Items, which implement this Interface, avoid the "isDamageable" and "isEnchantable" early return in the
 * {@linkplain net.minecraft.screen.AnvilScreenHandler AnvilScreenHandler} class and the
 * {@linkplain net.minecraft.screen.EnchantmentScreenHandler EnchantmentScreenHandler} class.
 * This is only needed when an item has a durability value of -1 (not breakable).<br><br>
 * The functionality is implemented in
 * {@linkplain net.shirojr.titanfabric.mixin.AnvilScreenHandlerMixin AnvilScreenHandlerMixin} and
 * {@linkplain net.shirojr.titanfabric.mixin.ItemMixin ItemMixin}
 */
public interface Anvilable {
}
