package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.shirojr.titanfabric.init.ConfigInit;
import net.shirojr.titanfabric.item.custom.armor.NetheriteArmorItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

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

    //FIXME: Renders wrong textures!!!
    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=netherite_helmet")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0)
    )
    private static ArmorItem titanfabric$netheriteHemlet(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        return new NetheriteArmorItem(EquipmentSlot.HEAD, settings);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=netherite_chestplate")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0)
    )
    private static ArmorItem titanfabric$netheriteChestplate(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        return new NetheriteArmorItem(EquipmentSlot.CHEST, settings);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=netherite_leggings")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0)
    )
    private static ArmorItem titanfabric$netheriteLeggings(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        return new NetheriteArmorItem(EquipmentSlot.LEGS, settings);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=netherite_boots")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0)
    )
    private static ArmorItem titanfabric$netheriteBoots(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        return new NetheriteArmorItem(EquipmentSlot.FEET, settings);
    }
}
