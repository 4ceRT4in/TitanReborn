package net.shirojr.titanfabric.mixin;

import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(SwordItem.class)
public class SwordItemMixin extends ToolItem implements Vanishable, WeaponEffectCrafting {

    public SwordItemMixin(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public Optional<ItemType> titanfabric$getCraftingType() {
        SwordItem swordItem = (SwordItem) (Object) this;
        if (swordItem.equals(Items.DIAMOND_SWORD)) return Optional.of(ItemType.PRODUCT);
        else return Optional.empty();
    }
}
