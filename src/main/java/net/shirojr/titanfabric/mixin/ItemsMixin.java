package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.*;
import net.shirojr.titanfabric.init.TitanFabricItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @ModifyArg(method = "<clinit>", slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=netherite_helmet")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem$Type;getMaxDamage(I)I"),
            index = 0
    )
    private static int modifyNetheriteArmorDurability(int multiplier) {
        return multiplier == 37 ? 74 : multiplier;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/item/AxeItem;createAttributeModifiers(Lnet/minecraft/item/ToolMaterial;FF)Lnet/minecraft/component/type/AttributeModifiersComponent;"),
            index = 1
    )
    private static float modifyAxeAttackDamage(float attackDamage) {
        return 2.0f;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/item/ShovelItem;createAttributeModifiers(Lnet/minecraft/item/ToolMaterial;FF)Lnet/minecraft/component/type/AttributeModifiersComponent;"),
            index = 1
    )
    private static float modifyShovelAttackDamage(float attackDamage) {
        return 0.0f;
    }

    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/item/SwordItem"
            )
    )
    private static SwordItem changeInstance(ToolMaterial toolMaterial, Item.Settings settings, Operation<SwordItem> original) {
        if (toolMaterial.equals(ToolMaterials.NETHERITE)) {
            return TitanFabricItems.NETHERITE_SWORD;
        }
        return original.call(toolMaterial, settings);
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;",
                    ordinal = 0
            ),
            slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/item/PotionItem;"))
    )
    private static int onPotion(int old) {
        return 3;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;",
                    ordinal = 0
            ),
            slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/item/SplashPotionItem;"))
    )
    private static int onSplashPotion(int old) {
        return 3;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;",
                    ordinal = 0
            ),
            slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/item/LingeringPotionItem;"))
    )
    private static int onLingeringPotion(int old) {
        return 3;
    }
}
