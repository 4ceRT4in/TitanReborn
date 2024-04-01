package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
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
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

import java.util.HashMap;
import java.util.UUID;

import static net.shirojr.titanfabric.network.NetworkingIdentifiers.*;

public class C2SNetworking {
    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(BOW_SCREEN_CHANNEL, C2SNetworking::handleBowScreenPacket);
        ServerPlayNetworking.registerGlobalReceiver(ARMOR_HANDLING_CHANNEL, C2SNetworking::handleArmorLifeHandlingPacket);
        ServerPlayNetworking.registerGlobalReceiver(EXTENDED_INVENTORY_OPEN, C2SNetworking::handleExtendedInventoryOpenPacket);
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
            TitanFabric.LOGGER.warn("User tried to use an unsupported feature");
            /*player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    // buf.writeItemStack(backpackItemStack);
                }

                @Override
                public Text getDisplayName() {
                    return player.getDisplayName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    //Inventory inventory = getInventoryFromNbt(backpackItemStack, backPackType); //TODO: get from persistent state
                    PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);

                    Inventory extendedInventory = new SimpleInventory(8);
                    if (persistentPlayerData != null) {
                        extendedInventory = persistentPlayerData.playerInventory;
                    }

                    return new ExtendedInventoryScreenHandler(syncId, playerInventory, extendedInventory);
                    // return new BackPackItemScreenHandler(syncId, playerInventory, inventory, backPackType, backpackItemStack);
                }
            });*/
        });
    }
}
