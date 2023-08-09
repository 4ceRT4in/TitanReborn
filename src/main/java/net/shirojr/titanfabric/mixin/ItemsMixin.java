package net.shirojr.titanfabric.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.ConfigInit;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=netherite_sword")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SwordItem;", ordinal = 0)
    )
    private static SwordItem titanfabric$netheriteSwordBalancing(ToolMaterial toolMaterial, int attackDamage,
                                                                 float attackSpeed, Item.Settings settings) {
        return new SwordItem(TitanFabricToolMaterials.NETHERITE, ConfigInit.CONFIG.netheriteSwordAttackDamage,
                ConfigInit.CONFIG.netheriteSwordAttackSpeed, settings.maxDamage(ConfigInit.CONFIG.netheriteSwordMaxDamage));
    }
}
