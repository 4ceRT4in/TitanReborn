package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.InstantHealthOrDamageStatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(InstantHealthOrDamageStatusEffect.class)
public class InstantHealthOrDamageStatusEffectMixin {
    @WrapOperation(method = "applyInstantEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean adjustDamage(LivingEntity instance, DamageSource source, float amount, Operation<Boolean> original) {
        float newAmount = amount;

        if (instance instanceof PlayerEntity player) {
            List<Item> armorSet = IntStream.rangeClosed(0, 3)
                    .mapToObj(player.getInventory()::getArmorStack)
                    .map(ItemStack::getItem).toList();
            int itemCount = 0;
            for (Item armorItem : armorSet) {
                if (armorItem instanceof CitrinArmorItem) itemCount++;
            }

            float chance = (float) itemCount / armorSet.size();
            newAmount = newAmount - (newAmount * chance);

            if (armorSet.stream().allMatch(item -> item instanceof CitrinArmorItem)) {
                newAmount = 0;
            }
        }

        return original.call(instance, source, newAmount);
    }
}
