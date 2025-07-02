package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ArrowSelectionPacket(int weaponStackIndex) implements CustomPayload {
    public static final Id<ArrowSelectionPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("arrow_selection"));

    public static final PacketCodec<RegistryByteBuf, ArrowSelectionPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ArrowSelectionPacket::weaponStackIndex,
            ArrowSelectionPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        PlayerInventory inventory = player.getInventory();
        ItemStack weaponStack = inventory.getStack(weaponStackIndex);
        if (!(weaponStack.getItem() instanceof SelectableArrow selectionHandler)) return;
        List<ItemStack> arrowStacks = ArrowSelectionHelper.findAllSupportedArrowStacks(inventory, selectionHandler);

        Map<Item, ItemStack> highestCountStacks = new HashMap<>();
        Map<String, ItemStack> effectBasedStacks = new HashMap<>();

        for (ItemStack stack : arrowStacks) {
            Item item = stack.getItem();
            if (!(item instanceof TitanFabricArrowItem)) {
                if (!highestCountStacks.containsKey(item) || stack.getCount() > highestCountStacks.get(item).getCount()) {
                    highestCountStacks.put(item, stack);
                }
            } else {
                WeaponEffectData.get(stack, WeaponEffectType.INNATE_EFFECT).ifPresentOrElse(effectData -> {
                    String effectKey = item + "-" + effectData.weaponEffect().name();
                    if (!effectBasedStacks.containsKey(effectKey) || stack.getCount() > effectBasedStacks.get(effectKey).getCount()) {
                        effectBasedStacks.put(effectKey, stack);
                    }
                }, () -> {
                    if (!highestCountStacks.containsKey(item) || stack.getCount() > highestCountStacks.get(item).getCount()) {
                        highestCountStacks.put(item, stack);
                    }
                });
            }
        }

        List<ItemStack> filteredArrowStacks = new ArrayList<>(highestCountStacks.values());
        filteredArrowStacks.addAll(effectBasedStacks.values());
        if (filteredArrowStacks.isEmpty()) return;
        ItemStack newSelectedArrowStack;

        Integer oldArrowStackIndex = selectionHandler.getSelectedIndex(weaponStack);
        if (oldArrowStackIndex == null) {
            newSelectedArrowStack = filteredArrowStacks.get(0);
        } else {
            ItemStack oldSelectedArrowStack = inventory.getStack(oldArrowStackIndex);
            if (filteredArrowStacks.contains(oldSelectedArrowStack)) {
                int newIndexInArrowList = filteredArrowStacks.indexOf(oldSelectedArrowStack) + 1;
                if (newIndexInArrowList > filteredArrowStacks.size() - 1) newIndexInArrowList = 0;
                newSelectedArrowStack = filteredArrowStacks.get(newIndexInArrowList);
            } else {
                newSelectedArrowStack = filteredArrowStacks.get(0);
            }
        }

        Text arrowStackName = newSelectedArrowStack.getItem().getName(newSelectedArrowStack);
        player.sendMessage(Text.translatable("actionbar.titanfabric.arrow_selection").append(arrowStackName), true);
        boolean changedComponent = selectionHandler.setSelectedIndex(inventory, weaponStack, inventory.main.indexOf(newSelectedArrowStack));
        LoggerUtil.devLogger("SelectedStack: %s | Changed final Stack Component: %s".formatted(newSelectedArrowStack.getName(), changedComponent));
    }
}
