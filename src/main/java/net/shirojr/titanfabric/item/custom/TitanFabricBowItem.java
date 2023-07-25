package net.shirojr.titanfabric.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BowItem;
import net.minecraft.text.LiteralText;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.TitanFabricClient;
import net.shirojr.titanfabric.event.TitanFabricKeyBindEvents;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.screen.ArrowSelectionScreen;

public class TitanFabricBowItem extends BowItem {
    public TitanFabricBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }

    @Environment(EnvType.CLIENT)
    public static void onClientTick(MinecraftClient client) {
        while (TitanFabricKeyBindEvents.BOW_SCREEN_KEY.wasPressed()) {
            if (client.player == null) return;
            var inventory = client.player.getInventory();
            if (inventory.getStack(inventory.selectedSlot).getItem() instanceof TitanFabricBowItem) {
                if (client.player == null) return;
                client.setScreen(new ArrowSelectionScreen(new LiteralText("Noice Title"), inventory.selectedSlot));
                TitanFabric.devLogger("opened arrow selection screen");
            }
        }
    }
}
