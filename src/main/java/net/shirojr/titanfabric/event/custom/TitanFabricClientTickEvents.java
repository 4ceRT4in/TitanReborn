package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.network.packet.ArmorLifePacket;
import net.shirojr.titanfabric.network.packet.ArrowSelectionPacket;
import net.shirojr.titanfabric.registry.KeyBindRegistry;
import net.shirojr.titanfabric.util.TitanFabricKeyBinds;

import java.util.List;
import java.util.Optional;
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
        int selectedSlot = client.player.getInventory().selectedSlot;

        if (TitanFabricKeyBinds.ARROW_SELECTION_KEY.isPressed()) {
            if (!keyBinds.wasPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY)) {
                new ArrowSelectionPacket(selectedSlot).sendPacket();
                keyBinds.setPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY, true);
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

        if (differenceOld.equals(Items.AIR)) {
            differenceOld = null;
        }
        if (differenceNew.equals(Items.AIR)) {
            differenceNew = null;
        }

        new ArmorLifePacket(
                Optional.ofNullable(differenceOld).map(Item::getDefaultStack),
                Optional.ofNullable(differenceNew).map(Item::getDefaultStack)
        ).sendPacket();
    }
}
