package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapedRecipe;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShapedRecipe.Serializer.class)
public class ShapedRecipeMixin {
    @ModifyReturnValue(method = "read(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/ShapedRecipe;", at = @At("RETURN"))
    private ShapedRecipe titanfabric$applyWeaponEffectToReadJson(ShapedRecipe original) {
        if (!(original.getOutput().getItem() instanceof WeaponEffectCrafting weaponEffect)) return original;
        ItemStack stack = original.getOutput();
        WeaponEffect baseEffect = weaponEffect.getBaseEffect();
        if (baseEffect != null) {
            WeaponEffectData innateEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, baseEffect, 1);
            EffectHelper.applyEffectToStack(stack, innateEffectData);
        }
        return new ShapedRecipe(original.getId(), original.getGroup(),
                original.getWidth(), original.getHeight(),
                original.getIngredients(), stack);
    }

    @ModifyReturnValue(method = "read(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/recipe/ShapedRecipe;", at = @At("RETURN"))
    private ShapedRecipe titanfabric$applyWeaponEffectToReadPacketByteBuff(ShapedRecipe original) {
        if (!(original.getOutput().getItem() instanceof WeaponEffectCrafting weaponEffect)) return original;
        ItemStack stack = original.getOutput();
        WeaponEffect baseEffect = weaponEffect.getBaseEffect();
        if (baseEffect != null) {
            WeaponEffectData innateEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, baseEffect, 1);
            EffectHelper.applyEffectToStack(stack, innateEffectData);
        }
        return new ShapedRecipe(original.getId(), original.getGroup(),
                original.getWidth(), original.getHeight(),
                original.getIngredients(), stack);
    }

    @WrapOperation(method = "write(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/recipe/ShapedRecipe;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;"))
    private PacketByteBuf titanfabric$applyWeaponEffectToWrite(PacketByteBuf instance, ItemStack stack, Operation<PacketByteBuf> original) {
        if (!(stack.getItem() instanceof WeaponEffectCrafting weaponEffect)){
            return original.call(instance, stack);
        }
        WeaponEffect baseEffect = weaponEffect.getBaseEffect();
        if (baseEffect != null) {
            WeaponEffectData innateEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, baseEffect, 1);
            EffectHelper.applyEffectToStack(stack, innateEffectData);
        }
        return original.call(instance, stack);
    }
}
