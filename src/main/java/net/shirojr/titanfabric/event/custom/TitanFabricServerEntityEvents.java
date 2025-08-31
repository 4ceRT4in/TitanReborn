package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.network.packet.ArmorDamageTiltFixPacket;

public class TitanFabricServerEntityEvents {
    public static class ArmorEvents implements ServerEntityEvents.EquipmentChange {
        @Override
        public void onChange(LivingEntity user, EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
            Item oldItem = oldStack.getItem();
            Item newItem = newStack.getItem();
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
