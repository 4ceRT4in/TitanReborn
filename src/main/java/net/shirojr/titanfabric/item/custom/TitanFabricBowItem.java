package net.shirojr.titanfabric.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.text.LiteralText;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.event.KeyBindEvents;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.screen.custom.ArrowSelectionScreen;
import net.shirojr.titanfabric.util.items.SelectableArrows;

public class TitanFabricBowItem extends BowItem implements SelectableArrows {
    public TitanFabricBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }

    @Environment(EnvType.CLIENT)
    public static void onClientTick(MinecraftClient client) {
        while (KeyBindEvents.BOW_SCREEN_KEY.wasPressed()) {
            if (client.player == null) return;
            PlayerInventory inventory = client.player.getInventory();
            if (inventory.getStack(inventory.selectedSlot).getItem() instanceof SelectableArrows) {
                if (client.player == null) return;
                client.setScreen(new ArrowSelectionScreen(new LiteralText("Noice Title"), inventory.selectedSlot));
                TitanFabric.devLogger("opened arrow selection screen");
            }
        }
    }
}
