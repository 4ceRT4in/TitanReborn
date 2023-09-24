package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {
    @Redirect(method = "applyInstantEffect",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean test(LivingEntity instance, DamageSource source, float amount) {
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

        return instance.damage(source, newAmount);
    }
}
