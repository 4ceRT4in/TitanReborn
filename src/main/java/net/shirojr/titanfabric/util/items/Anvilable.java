package net.shirojr.titanfabric.util.items;

/**
 * Items, which implement this Interface, avoid the "isDamageable" early return in the
 * {@linkplain net.minecraft.screen.AnvilScreenHandler AnvilScreenHandler} class.
 * This is only needed when an item has a durability value of -1 (not breakable).<br><br>
 * The functionality is implemented in
 * {@linkplain net.shirojr.titanfabric.mixin.AnvilScreenHandlerMixin AnvilScreenHandlerMixin}
 */
public interface Anvilable {
}
