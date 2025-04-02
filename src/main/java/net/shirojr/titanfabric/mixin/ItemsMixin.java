package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.shirojr.titanfabric.init.TitanFabricItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @WrapOperation(method = "<clinit>", slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=netherite_helmet")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem$Type;getMaxDamage(I)I")
    )
    private static int modifyNetheriteArmorDurability(ArmorItem.Type instance, int multiplier, Operation<Integer> original) {
        if (multiplier == 37) return 74;
        return multiplier;
    }


    @Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=netherite_sword")),
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0)
    )
    private static Item registerNetheriteSword(Item.Settings settings) {
        return TitanFabricItems.NETHERITE_SWORD;
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
