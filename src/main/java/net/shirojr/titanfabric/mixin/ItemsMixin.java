package net.shirojr.titanfabric.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.shirojr.titanfabric.init.ConfigInit;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=wooden_sword")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SwordItem;", ordinal = 0)
    )
    private static SwordItem titanfabric$woodenSwordBalancing(ToolMaterial toolMaterial, int attackDamage,
                                                              float attackSpeed, Item.Settings settings) {
        return new SwordItem(TitanFabricToolMaterials.WOOD, ConfigInit.CONFIG.woodenSwordAttackDamage,
                ConfigInit.CONFIG.woodenSwordAttackSpeed, settings.maxDamage(ConfigInit.CONFIG.woodenSwordMaxDamage));
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=golden_sword")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SwordItem;", ordinal = 0)
    )
    private static SwordItem titanfabric$goldenSwordBalancing(ToolMaterial toolMaterial, int attackDamage,
                                                              float attackSpeed, Item.Settings settings) {
        return new SwordItem(TitanFabricToolMaterials.GOLD, ConfigInit.CONFIG.goldenSwordAttackDamage,
                ConfigInit.CONFIG.goldenSwordAttackSpeed, settings.maxDamage(ConfigInit.CONFIG.goldenSwordMaxDamage));
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone_sword")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SwordItem;", ordinal = 0)
    )
    private static SwordItem titanfabric$stoneSwordBalancing(ToolMaterial toolMaterial, int attackDamage,
                                                             float attackSpeed, Item.Settings settings) {
        return new SwordItem(TitanFabricToolMaterials.STONE, ConfigInit.CONFIG.stoneSwordAttackDamage,
                ConfigInit.CONFIG.stoneSwordAttackSpeed, settings.maxDamage(ConfigInit.CONFIG.stoneSwordMaxDamage));
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=iron_sword")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SwordItem;", ordinal = 0)
    )
    private static SwordItem titanfabric$ironSwordBalancing(ToolMaterial toolMaterial, int attackDamage,
                                                            float attackSpeed, Item.Settings settings) {
        return new SwordItem(TitanFabricToolMaterials.IRON, ConfigInit.CONFIG.ironSwordAttackDamage,
                ConfigInit.CONFIG.ironSwordAttackSpeed, settings.maxDamage(ConfigInit.CONFIG.ironSwordMaxDamage));
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=diamond_sword")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SwordItem;", ordinal = 0)
    )
    private static SwordItem titanfabric$diamondSwordBalancing(ToolMaterial toolMaterial, int attackDamage,
                                                               float attackSpeed, Item.Settings settings) {
        return new SwordItem(TitanFabricToolMaterials.DIAMOND, ConfigInit.CONFIG.diamondSwordAttackDamage,
                ConfigInit.CONFIG.diamondSwordAttackSpeed, settings.maxDamage(ConfigInit.CONFIG.diamondSwordMaxDamage));
    }

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
