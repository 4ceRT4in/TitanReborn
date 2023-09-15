package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void titanfabric$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source != DamageSource.IN_FIRE && source != DamageSource.ON_FIRE && source != DamageSource.HOT_FLOOR) return;
        if (!(((LivingEntity) (Object) this) instanceof PlayerEntity player)) return;

        List<Item> armorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();

        armorSet.forEach(item -> {
            if (item instanceof NetherArmorItem) {
                if (player.getWorld().getRandom().nextInt(4) > 0) {
                    player.extinguish();
                    cir.setReturnValue(false);
                }
            }
        });
    }

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            cancellable = true, at = @At("HEAD"))
    private void titanfabric$addStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof PlayerEntity player) || effect.getEffectType().isBeneficial()) return;

        List<Item> armorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();

        if (armorSet.stream().allMatch(item -> item instanceof CitrinArmorItem)) {
            cir.setReturnValue(false);
            return;
        }

        armorSet.forEach(item -> {
            if (item instanceof CitrinArmorItem) {
                if (player.getRandom().nextInt(0, 3) < 1) {
                    cir.setReturnValue(false);
                }
            }
        });
    }
}
