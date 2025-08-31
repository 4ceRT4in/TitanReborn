package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.network.packet.ArrowSelectionPacket;
import net.shirojr.titanfabric.registry.KeyBindRegistry;
import net.shirojr.titanfabric.util.TitanFabricKeyBinds;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.items.SelectableArrow;

import java.util.List;
import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectType.INNATE_EFFECT;

public class TitanFabricClientTickEvents {
    private TitanFabricClientTickEvents() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(TitanFabricClientTickEvents::handleKeyBindEvent);
        ItemTooltipCallback.EVENT.register(TitanFabricClientTickEvents::handleTooltip);
    }

    private static void handleTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (stack.getItem() != TitanFabricItems.LEGEND_BOW) return;
        ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(stack, client.player);
        if (selectedArrowStack == null) {
            lines.add(Text.translatable("tooltip.titanfabric.legend_bow_arrow", "Normal").formatted(Formatting.GRAY));
            return;
        }
        if (!(selectedArrowStack.getItem() instanceof TitanFabricArrowItem)) {
            lines.add(Text.translatable("tooltip.titanfabric.legend_bow_arrow", "Normal").formatted(Formatting.GRAY));
            return;
        }

        Optional<WeaponEffectData> effectData = WeaponEffectData.get(selectedArrowStack, INNATE_EFFECT);
        if (effectData.isEmpty() || effectData.get().weaponEffect() == null) {
            lines.add(Text.translatable("tooltip.titanfabric.legend_bow_arrow", "Normal").formatted(Formatting.GRAY));
            return;
        }

        String effectName = switch (effectData.get().weaponEffect()) {
            case BLIND -> "Blindness";
            case FIRE -> "Fire";
            case POISON -> "Poison";
            case WEAK -> "Weakness";
            case WITHER -> "Wither";
        };

        Formatting effectColor = switch (effectData.get().weaponEffect()) {
            case BLIND -> Formatting.DARK_BLUE;
            case FIRE -> Formatting.GOLD;
            case POISON -> Formatting.DARK_GREEN;
            case WEAK -> Formatting.GRAY;
            case WITHER -> Formatting.DARK_GRAY;
        };

        lines.add(Text.translatable("tooltip.titanfabric.legend_bow_arrow", effectName).formatted(effectColor));
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
}
