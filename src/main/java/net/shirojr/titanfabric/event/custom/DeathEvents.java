package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import net.shirojr.titanfabric.effect.ImmunityEffect;

public class DeathEvents {
    public static void register() {
        ServerPlayerEvents.COPY_FROM.register(DeathEvents::handleRespawnData);
    }

    private static void handleRespawnData(ServerPlayerEntity before, ServerPlayerEntity after, boolean isAlive) {
        ImmunityEffect.resetImmunity(after);

        handleExtendedInventories(
                ExtendedInventoryComponent.getEntity(before),
                ExtendedInventoryComponent.getEntity(after)
        );
        if (before.getScoreboardTeam() != null && after.getScoreboardTeam() != null) {
            handleExtendedInventories(
                    ExtendedInventoryComponent.getTeam(before.getScoreboardTeam()),
                    ExtendedInventoryComponent.getTeam(after.getScoreboardTeam())
            );
        }
    }

    private static void handleExtendedInventories(ExtendedInventoryComponent oldInstance, ExtendedInventoryComponent newInstance) {
        if (oldInstance.shouldDropInventory() || oldInstance.equals(newInstance)) {
            return;
        }
        newInstance.modifyInventory(newInventory -> {
            newInventory.clear();
            Inventory oldInventory = oldInstance.getInventory();
            for (int i = 0; i < oldInventory.size(); i++) {
                ItemStack stack = oldInventory.getStack(i);
                newInventory.setStack(i, stack);
            }
        }, true);
    }
}
