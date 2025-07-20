package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;

public class DeathEvents {
    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register(DeathEvents::afterRespawn);
    }

    private static void afterRespawn(ServerPlayerEntity before, ServerPlayerEntity after, boolean isAlive) {
        ServerWorld world = after.getServerWorld();
        handleExtendedInventories(
                ExtendedInventoryComponent.getEntity(before),
                ExtendedInventoryComponent.getEntity(after),
                world, before.getBlockPos()
        );
        if (before.getScoreboardTeam() != null && after.getScoreboardTeam() != null) {
            handleExtendedInventories(
                    ExtendedInventoryComponent.getTeam(before.getScoreboardTeam()),
                    ExtendedInventoryComponent.getTeam(after.getScoreboardTeam()),
                    world, before.getBlockPos()
            );
        }
    }

    private static void handleExtendedInventories(ExtendedInventoryComponent oldInstance, ExtendedInventoryComponent newInstance,
                                                  World world, BlockPos pos) {
        if (oldInstance.shouldDropInventory()) {
            ItemScatterer.spawn(world, pos, oldInstance.getInventory());
            oldInstance.getInventory().clear();
            oldInstance.sync();
            return;
        }
        if (oldInstance.equals(newInstance)) {
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
