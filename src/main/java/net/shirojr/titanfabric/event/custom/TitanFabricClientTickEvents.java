package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.network.NetworkingIdentifiers;
import net.shirojr.titanfabric.registry.KeyBindRegistry;
import net.shirojr.titanfabric.util.TitanFabricKeyBinds;
import net.shirojr.titanfabric.util.LoggerUtil;

import java.util.List;
import java.util.stream.IntStream;

public class TitanFabricClientTickEvents {
    private static List<Item> armorList = List.of(Items.AIR, Items.AIR, Items.AIR, Items.AIR);

    private TitanFabricClientTickEvents() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(TitanFabricClientTickEvents::handleKeyBindEvent);
        ClientTickEvents.END_CLIENT_TICK.register(TitanFabricClientTickEvents::handleArmorTickEvent);
    }

    private static void handleKeyBindEvent(MinecraftClient client) {
        if (client.player == null) return;
        KeyBindRegistry keyBinds = KeyBindRegistry.getInstance();

        if (TitanFabricKeyBinds.ARROW_SELECTION_KEY.isPressed()) {
            if (!keyBinds.wasPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY)) {
                ClientPlayNetworking.send(NetworkingIdentifiers.ARROW_SELECTION, PacketByteBufs.create());
                keyBinds.setPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY, true);
                LoggerUtil.devLogger("Executed Arrow Selection On Client Side");
            }
        } else {
            keyBinds.setPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY, false);
        }
    }

    private static void handleArmorTickEvent(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;

        List<Item> currentArmorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();

        if (armorList.equals(currentArmorSet)) return;

        Item differenceOld = null, differenceNew = null;
        for (int i = 0; i < currentArmorSet.size(); i++) {
            if (!currentArmorSet.get(i).equals(armorList.get(i))) {
                differenceOld = armorList.get(i);
                differenceNew = currentArmorSet.get(i);
                break;
            }
        }

        armorList = currentArmorSet;
        if (!(differenceOld instanceof LegendArmorItem) && !(differenceNew instanceof LegendArmorItem)) return;
        if (differenceOld instanceof LegendArmorItem && differenceNew instanceof LegendArmorItem) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(differenceOld.getDefaultStack());
        buf.writeItemStack(differenceNew.getDefaultStack());

        ClientPlayNetworking.send(NetworkingIdentifiers.ARMOR_HANDLING_CHANNEL, buf);
    }
}
