package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.network.packet.ArmorDamageTiltFixPacket;
import net.shirojr.titanfabric.util.items.ArmorHelper;

public class TitanFabricServerEntityEvents {
    public static class ArmorEvents implements ServerEntityEvents.EquipmentChange {
        @Override
        public void onChange(LivingEntity user, EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
            handleLegendArmor(user, oldStack, newStack);
            handleNetheriteArmor(user);
        }

        private void handleNetheriteArmor(LivingEntity user) {
            if(!(user instanceof ServerPlayerEntity player)) return;

            int count = ArmorHelper.getNetheriteArmorCount(player);
            boolean full = (count == 4);

            if (full) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, -1, 0, false, false, true));
            } else {
                StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.RESISTANCE);
                if(statusEffectInstance == null) return;
                if(statusEffectInstance.getDuration() <= -1) {
                    player.removeStatusEffect(statusEffectInstance.getEffectType());
                }
            }
        }

        private void handleLegendArmor(LivingEntity user, ItemStack oldStack, ItemStack newStack) {
            Item oldItem = oldStack.getItem();
            Item newItem = newStack.getItem();
            if (oldItem instanceof LegendArmorItem ^ newItem instanceof LegendArmorItem) {
                FrostburnComponent frostburnComponent = FrostburnComponent.get(user);
                frostburnComponent.equipmentChange(user, oldStack, newStack);
            }

            if (newItem instanceof LegendArmorItem) {
                return;
            }
            if (!(oldItem instanceof LegendArmorItem oldLegendArmorItem)) {
                return;
            }
            float currentHealth = user.getHealth();
            float maxHealth = user.getMaxHealth();

            if (currentHealth > (maxHealth - oldLegendArmorItem.getExtraValue())) {
                user.setHealth(maxHealth - oldLegendArmorItem.getExtraValue());
            }
            if (user instanceof ServerPlayerEntity player) {
                new ArmorDamageTiltFixPacket(oldStack).sendPacket(player);
            }
        }
    }
}
