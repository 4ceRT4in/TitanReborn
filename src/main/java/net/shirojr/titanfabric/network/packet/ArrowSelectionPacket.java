package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.*;

public record ArrowSelectionPacket() implements CustomPayload {
    public static final Id<ArrowSelectionPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("arrow_selection"));

    public static final PacketCodec<RegistryByteBuf, ArrowSelectionPacket> CODEC = PacketCodec.unit(new ArrowSelectionPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
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
                Optional<WeaponEffectData> effectData = WeaponEffectData.get(stack, WeaponEffectType.INNATE_EFFECT);

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

        if (filteredArrowStacks.isEmpty()) return;
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
        player.sendMessage(Text.translatable("actionbar.titanfabric.arrow_selection").append(arrowStackName), true);
        arrowSelection.titanfabric$setSelectedArrowIndex(newSelectedArrowStack);
        LoggerUtil.devLogger("SelectedStack: " + newSelectedArrowStack.getName());
    }
}
