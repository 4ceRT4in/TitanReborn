package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SmithingRecipe.class)
public abstract class SmithingRecipeMixin {
    @ModifyReturnValue(method = "craft", at = @At("RETURN"))
    private ItemStack test(ItemStack original) {
        if (!(original.getItem() instanceof TitanFabricSwordItem swordItem)) return original;
        if (swordItem.canHaveWeaponEffects()) return original;
        original.getOrCreateNbt().remove(WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY);
        return original;
    }
}
