package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.List;

import static net.shirojr.titanfabric.network.NetworkingIdentifiers.*;

public class C2SNetworking {
    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(BOW_SCREEN_CHANNEL, C2SNetworking::handleBowScreenPacket);
        ServerPlayNetworking.registerGlobalReceiver(ARMOR_HANDLING_CHANNEL, C2SNetworking::handleArmorLifeHandlingPacket);
        ServerPlayNetworking.registerGlobalReceiver(EXTENDED_INVENTORY_OPEN, C2SNetworking::handleExtendedInventoryOpenPacket);
        ServerPlayNetworking.registerGlobalReceiver(ARROW_SELECTION, C2SNetworking::handleArrowSelectionPacket);
        ServerPlayNetworking.registerGlobalReceiver(NETHERITE_ANVIL_USE, C2SNetworking::netheriteAnvilUsePacket);
    }

    private static void handleBowScreenPacket(MinecraftServer server, ServerPlayerEntity player,
                                              ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ItemStack selectedStack = buf.readItemStack();

        server.execute(() -> {
            player.sendMessage(new LiteralText("Arrow Packet arrived on server side"), false);
            player.sendMessage(new LiteralText("Selected Arrow: " + selectedStack), false);
        });
    }

    private static void handleArmorLifeHandlingPacket(MinecraftServer server, ServerPlayerEntity player,
                                                      ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        Item oldItem = buf.readItemStack().getItem();
        Item newItem = buf.readItemStack().getItem();

        server.execute(() -> {
            float healthValue;

            if (oldItem instanceof LegendArmorItem legendArmorItem) {
                healthValue = legendArmorItem.getHealthValue();

                if (player.getHealth() > healthValue) {
                    player.setHealth(player.getMaxHealth() - healthValue);
                }
            }

            if (newItem instanceof LegendArmorItem legendArmorItem) {
                healthValue = legendArmorItem.getHealthValue();

                if (player.getHealth() > healthValue) {
                    player.setHealth(player.getMaxHealth() + healthValue);
                }
            }
        });
    }

    private static void handleExtendedInventoryOpenPacket(MinecraftServer server, ServerPlayerEntity player,
                                                          ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {

        server.execute(() -> {
            List<String> description = List.of(player.getDisplayName().asString());
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeCollection(description, PacketByteBuf::writeString);
                }

                @Override
                public Text getDisplayName() {
                    return player.getDisplayName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);
                    Inventory extendedInventory = new SimpleInventory(8);
                    if (persistentPlayerData != null) {
                        extendedInventory = persistentPlayerData.extraInventory;
                    }
                    return new ExtendedInventoryScreenHandler(syncId, playerInventory, extendedInventory, description);
                }
            });
        });
    }

    private static void handleArrowSelectionPacket(MinecraftServer server, ServerPlayerEntity player,
                                                   ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf buf,
                                                   PacketSender sender) {
        server.execute(() -> {
            SelectableArrows bowItem = null;
            if (player.getOffHandStack().getItem() instanceof SelectableArrows selectableArrows)
                bowItem = selectableArrows;
            if (player.getMainHandStack().getItem() instanceof SelectableArrows selectableArrows)
                bowItem = selectableArrows;
            if (bowItem == null) return;

            ArrowSelectionHandler arrowSelection = (ArrowSelectionHandler) player;
            PlayerInventory inventory = player.getInventory();
            List<ItemStack> arrowStacks = ArrowSelectionHelper.findAllSupportedArrowStacks(inventory, bowItem);
            if (arrowStacks.size() == 0) return;
            ItemStack newSelectedArrowStack;

            if (arrowSelection.titanfabric$getSelectedArrowIndex().isPresent()) {
                ItemStack selectedArrowStack = player.getInventory().getStack(arrowSelection.titanfabric$getSelectedArrowIndex().get());
                if (arrowStacks.contains(selectedArrowStack)) {
                    int newIndexInArrowList = arrowStacks.indexOf(selectedArrowStack) + 1;
                    if (newIndexInArrowList > arrowStacks.size() - 1) newIndexInArrowList = 0;
                    newSelectedArrowStack = arrowStacks.get(newIndexInArrowList);
                } else {
                    newSelectedArrowStack = arrowStacks.get(0);
                }
            } else {
                newSelectedArrowStack = arrowStacks.get(0);
            }
            Text arrowStackName = newSelectedArrowStack.getItem().getName(newSelectedArrowStack);
            player.sendMessage(new TranslatableText("actionbar.titanfabric.arrow_selection").append(arrowStackName), true);
            arrowSelection.titanfabric$setSelectedArrowIndex(newSelectedArrowStack);
            LoggerUtil.devLogger("SelectedStack: " + newSelectedArrowStack.getName());
        });
    }

    private static void netheriteAnvilUsePacket(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf buf,
                                                PacketSender sender) {
        BlockPos pos = buf.readBlockPos();
        server.execute(() ->
                player.getWorld().playSound(null, pos, SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.BLOCKS, 1f, 1f)
        );
    }
}
