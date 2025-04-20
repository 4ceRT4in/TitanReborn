package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.shirojr.titanfabric.TitanFabricClient;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.network.NetworkingIdentifiers;
import net.shirojr.titanfabric.registry.KeyBindRegistry;
import net.shirojr.titanfabric.util.TitanFabricKeyBinds;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;

import java.util.List;
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
                ClientPlayNetworking.send(NetworkingIdentifiers.ARROW_SELECTION, PacketByteBufs.create());
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
                    TitanFabricClient.soulFireEntities.remove(entity.getUuid());
                    return;
                }
                Box box = entity.getBoundingBox();
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
                BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

                for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                    for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                        for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                            mutable.set(i, j, k);
                            if (client.world.getBlockState(mutable).getBlock() == Blocks.SOUL_FIRE) {
                                TitanFabricClient.soulFireEntities.add(entity.getUuid());
                                return;
                            }
                            if (client.world.getBlockState(mutable).getBlock() == Blocks.FIRE) {
                                TitanFabricClient.soulFireEntities.remove(entity.getUuid());
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

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(differenceOld.getDefaultStack());
        buf.writeItemStack(differenceNew.getDefaultStack());

        ClientPlayNetworking.send(NetworkingIdentifiers.ARMOR_HANDLING_CHANNEL, buf);
    }
}
