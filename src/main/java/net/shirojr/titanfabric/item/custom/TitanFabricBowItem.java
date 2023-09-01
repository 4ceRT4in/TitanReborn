package net.shirojr.titanfabric.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.event.KeyBindEvents;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.screen.custom.ArrowSelectionScreen;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.List;

public class TitanFabricBowItem extends BowItem {
    public TitanFabricBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }

    @Environment(EnvType.CLIENT)
    public static void onClientTick(MinecraftClient client) {
        while (KeyBindEvents.BOW_SCREEN_KEY.wasPressed()) {
            if (client.player == null) return;
            if (!(client.player.getMainHandStack().getItem() instanceof SelectableArrows bowStack)) return;

            PlayerInventory inventory = client.player.getInventory();
            List<ItemStack> arrowStacks = inventory.main.stream()
                    .filter(itemStack -> bowStack.supportedArrows().contains(itemStack.getItem())).toList();

            client.setScreen(new ArrowSelectionScreen(new LiteralText("Noice Title"), arrowStacks));
            TitanFabric.devLogger("opened arrow selection screen");
        }
    }
}
