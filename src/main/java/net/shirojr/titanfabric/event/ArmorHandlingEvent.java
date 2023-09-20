package net.shirojr.titanfabric.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.network.TitanFabricNetworking;

import java.util.List;
import java.util.stream.IntStream;

public class ArmorHandlingEvent {
    private List<Item> armorList = List.of(Items.AIR, Items.AIR, Items.AIR, Items.AIR);

    private void tickHandling() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            if (player == null) return;

            List<Item> currentArmorSet = IntStream.rangeClosed(0, 3)
                    .mapToObj(player.getInventory()::getArmorStack)
                    .map(ItemStack::getItem).toList();

            if (this.armorList.equals(currentArmorSet)) return;

            Item differenceOld = null, differenceNew = null;
            for (int i = 0; i < currentArmorSet.size(); i++) {
                if (!currentArmorSet.get(i).equals(armorList.get(i))) {
                    differenceOld = armorList.get(i);
                    differenceNew = currentArmorSet.get(i);
                    break;
                }
            }

            this.armorList = currentArmorSet;
            if (!(differenceOld instanceof LegendArmorItem) && !(differenceNew instanceof LegendArmorItem)) return;
            if (differenceOld instanceof LegendArmorItem && differenceNew instanceof LegendArmorItem) return;

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeItemStack(differenceOld.getDefaultStack());
            buf.writeItemStack(differenceNew.getDefaultStack());

            ClientPlayNetworking.send(TitanFabricNetworking.ARMOR_HANDLING_CHANNEL, buf);
        });
    }

    public static void register() {
        ArmorHandlingEvent armorHandler = new ArmorHandlingEvent();
        armorHandler.tickHandling();
    }
}