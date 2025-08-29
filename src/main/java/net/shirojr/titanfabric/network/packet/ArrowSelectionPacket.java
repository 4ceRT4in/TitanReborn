package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
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

import java.util.*;

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

        List<ItemStack> arrowStacks = ArrowSelectionHelper.findAllSupportedArrowStacks(player, selectionHandler);

        Map<Item, ItemStack> highestCountStacks = new HashMap<>();
        Map<String, ItemStack> effectBasedStacks = new HashMap<>();

        for (ItemStack arrowStack : arrowStacks) {
            Item arrowItem = arrowStack.getItem();
            if (arrowItem instanceof TitanFabricArrowItem) {
                WeaponEffectData.get(arrowStack, WeaponEffectType.INNATE_EFFECT).ifPresentOrElse(effectData -> {
                    String effectKey = arrowItem + "-" + effectData.weaponEffect().name();
                    if (!effectBasedStacks.containsKey(effectKey) || arrowStack.getCount() > effectBasedStacks.get(effectKey).getCount()) {
                        effectBasedStacks.put(effectKey, arrowStack);
                    }
                }, () -> {
                    if (!highestCountStacks.containsKey(arrowItem) || arrowStack.getCount() > highestCountStacks.get(arrowItem).getCount()) {
                        highestCountStacks.put(arrowItem, arrowStack);
                    }
                });
            } else if (arrowItem instanceof PotionItem) {
                PotionContentsComponent arrowStackComponent = arrowStack.get(DataComponentTypes.POTION_CONTENTS);
                if (arrowStackComponent == null) continue;
                StringBuilder potionContentComposer = new StringBuilder(arrowItem.toString());
                for (StatusEffectInstance effect : arrowStackComponent.getEffects()) {
                    potionContentComposer.append('-');
                    potionContentComposer.append(effect.getEffectType().getIdAsString());
                }
                String potionContent = potionContentComposer.toString();
                if (effectBasedStacks.containsKey(potionContent) && arrowStack.getCount() <= effectBasedStacks.get(potionContent).getCount()) {
                    continue;
                }
                effectBasedStacks.put(potionContent, arrowStack);
            } else {
                if (!highestCountStacks.containsKey(arrowItem) || arrowStack.getCount() > highestCountStacks.get(arrowItem).getCount()) {
                    highestCountStacks.put(arrowItem, arrowStack);
                }
            }
        }

        List<ItemStack> filteredArrowStacks = new ArrayList<>(highestCountStacks.values());
        filteredArrowStacks.addAll(effectBasedStacks.values());

        if (filteredArrowStacks.isEmpty()) return;
        ItemStack newSelectedArrowStack;

        ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(weaponStack, player);
        if (selectedArrowStack == null) {
            newSelectedArrowStack = filteredArrowStacks.get(0);
        } else {
            if (filteredArrowStacks.contains(selectedArrowStack)) {
                int newIndexInArrowList = filteredArrowStacks.indexOf(selectedArrowStack) + 1;
                if (newIndexInArrowList > filteredArrowStacks.size() - 1) newIndexInArrowList = 0;
                newSelectedArrowStack = filteredArrowStacks.get(newIndexInArrowList);
            } else {
                newSelectedArrowStack = filteredArrowStacks.get(0);
            }
        }

        Text arrowStackName = newSelectedArrowStack.getItem().getName(newSelectedArrowStack);
        player.sendMessage(Text.translatable("actionbar.titanfabric.arrow_selection").append(arrowStackName), true);

        Optional<SelectableArrow.Index> newStackIndex = SelectableArrow.Index.get(player, newSelectedArrowStack);

        boolean changedComponent = false;
        if (newStackIndex.isPresent()) {
            changedComponent = SelectableArrow.applySelectedArrowStack(weaponStack, newStackIndex.orElse(null));
        }

        LoggerUtil.devLogger("SelectedStack: %s | Changed final Stack Component: %s".formatted(newSelectedArrowStack.getName(), changedComponent));
    }
}
