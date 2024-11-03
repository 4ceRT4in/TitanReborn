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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.*;

import static net.shirojr.titanfabric.network.NetworkingIdentifiers.*;

public class C2SNetworking {
    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(BOW_SCREEN_CHANNEL, C2SNetworking::handleBowScreenPacket);
        ServerPlayNetworking.registerGlobalReceiver(ARMOR_HANDLING_CHANNEL, C2SNetworking::handleArmorLifeHandlingPacket);
        ServerPlayNetworking.registerGlobalReceiver(EXTENDED_INVENTORY_OPEN, C2SNetworking::handleExtendedInventoryOpenPacket);
        ServerPlayNetworking.registerGlobalReceiver(ARROW_SELECTION, C2SNetworking::handleArrowSelectionPacket);
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
            // Only handle the case where we're removing Legend armor
            // When equipping new armor, the attribute system handles it automatically
            if (oldItem instanceof LegendArmorItem legendArmorItem) {
                float currentHealth = player.getHealth();
                float maxHealth = player.getMaxHealth();

                // Force an update by setting health to max if it's above the new max health
                // This fixes the vanilla visual desync
                if (currentHealth > (maxHealth - legendArmorItem.getHealthValue())) {
                    player.setHealth(maxHealth - legendArmorItem.getHealthValue());
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

            // Filter to keep only the stack with the highest count of each type or unique effect (for TitanFabricArrowItem)
            List<ItemStack> filteredArrowStacks = new ArrayList<>();
            Map<Item, ItemStack> highestCountStacks = new HashMap<>();
            Map<String, ItemStack> effectBasedStacks = new HashMap<>();

            for (ItemStack stack : arrowStacks) {
                Item item = stack.getItem();

                // Special case for TitanFabricArrowItem - check both item and weapon effect for uniqueness
                if (item instanceof TitanFabricArrowItem) {
                    Optional<WeaponEffectData> effectData = WeaponEffectData.fromNbt(
                            stack.getOrCreateNbt().getCompound(WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY), WeaponEffectType.INNATE_EFFECT);

                    // If effect data is present, use it to determine uniqueness
                    if (effectData.isPresent()) {
                        String effectKey = item.toString() + "-" + effectData.get().weaponEffect().name();

                        if (!effectBasedStacks.containsKey(effectKey) || stack.getCount() > effectBasedStacks.get(effectKey).getCount()) {
                            effectBasedStacks.put(effectKey, stack);
                        }
                    } else {
                        // Fallback to general count tracking if no effect data is present
                        if (!highestCountStacks.containsKey(item) || stack.getCount() > highestCountStacks.get(item).getCount()) {
                            highestCountStacks.put(item, stack);
                        }
                    }
                } else {
                    // General case for non-TitanFabricArrowItem items, grouped by item type only
                    if (!highestCountStacks.containsKey(item) || stack.getCount() > highestCountStacks.get(item).getCount()) {
                        highestCountStacks.put(item, stack);
                    }
                }
            }

            // Combine both maps into filteredArrowStacks
            filteredArrowStacks.addAll(highestCountStacks.values());
            filteredArrowStacks.addAll(effectBasedStacks.values());

            if (filteredArrowStacks.size() == 0) return;
            ItemStack newSelectedArrowStack;

            if (arrowSelection.titanfabric$getSelectedArrowIndex().isPresent()) {
                ItemStack selectedArrowStack = player.getInventory().getStack(arrowSelection.titanfabric$getSelectedArrowIndex().get());
                if (filteredArrowStacks.contains(selectedArrowStack)) {
                    int newIndexInArrowList = filteredArrowStacks.indexOf(selectedArrowStack) + 1;
                    if (newIndexInArrowList > filteredArrowStacks.size() - 1) newIndexInArrowList = 0;
                    newSelectedArrowStack = filteredArrowStacks.get(newIndexInArrowList);
                } else {
                    newSelectedArrowStack = filteredArrowStacks.get(0);
                }
            } else {
                newSelectedArrowStack = filteredArrowStacks.get(0);
            }

            Text arrowStackName = newSelectedArrowStack.getItem().getName(newSelectedArrowStack);
            player.sendMessage(new TranslatableText("actionbar.titanfabric.arrow_selection").append(arrowStackName), true);
            arrowSelection.titanfabric$setSelectedArrowIndex(newSelectedArrowStack);
            LoggerUtil.devLogger("SelectedStack: " + newSelectedArrowStack.getName());
        });
    }



}
