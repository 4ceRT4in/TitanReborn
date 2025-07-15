package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.shirojr.titanfabric.TitanFabricClient;
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
        ClientTickEvents.END_CLIENT_TICK.register(TitanFabricClientTickEvents::handleSoulFireEvent);
    }

    private static void handleKeyBindEvent(MinecraftClient client) {
        if (client.player == null) return;
        KeyBindRegistry keyBinds = KeyBindRegistry.getInstance();

        if (TitanFabricKeyBinds.ARROW_SELECTION_KEY.isPressed()) {
            if (!keyBinds.wasPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY)) {
                new ArrowSelectionPacket().sendPacket();
                keyBinds.setPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY, true);
            }
        } else {
            keyBinds.setPressed(TitanFabricKeyBinds.ARROW_SELECTION_KEY, false);
        }
    }

    private static void handleSoulFireEvent(MinecraftClient client) {
        if (client.world != null) {
            client.world.getEntities().forEach(entity -> {
                if (entity.isInLava()) {
                    TitanFabricClient.SOUL_FIRE_ENTITIES.remove(entity.getUuid());
                    return;
                }
                Box box = entity.getBoundingBox();
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                BlockPos blockPos = new BlockPos((int) (box.minX + 0.001), (int) (box.minY + 0.001), (int) (box.minZ + 0.001));
                BlockPos blockPos2 = new BlockPos((int) (box.maxX - 0.001), (int) (box.maxY - 0.001), (int) (box.maxZ - 0.001));

                for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                    for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                        for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                            mutable.set(i, j, k);
                            if (client.world.getBlockState(mutable).getBlock() == Blocks.SOUL_FIRE) {
                                TitanFabricClient.SOUL_FIRE_ENTITIES.add(entity.getUuid());
                                return;
                            }
                            if (client.world.getBlockState(mutable).getBlock() == Blocks.FIRE) {
                                TitanFabricClient.SOUL_FIRE_ENTITIES.remove(entity.getUuid());
                                return;
                            }
                        }
                    }
                }
            });
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
