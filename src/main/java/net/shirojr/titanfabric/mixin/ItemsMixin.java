package net.shirojr.titanfabric.mixin;

import net.minecraft.item.*;
import net.shirojr.titanfabric.item.TitanFabricItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class ItemsMixin {

    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=netherite_sword")), at = @At(value = "NEW", target = "(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SwordItem;", ordinal = 0))
    private static SwordItem titanfabric$netheriteSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        return TitanFabricItems.NETHERITE_SWORD;
    }

}
