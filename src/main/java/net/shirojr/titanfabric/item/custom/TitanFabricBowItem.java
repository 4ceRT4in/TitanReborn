package net.shirojr.titanfabric.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.LiteralText;
import net.shirojr.titanfabric.event.KeyBindEvents;
import net.shirojr.titanfabric.screen.screen.ArrowSelectionScreen;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public abstract class TitanFabricBowItem extends BowItem implements SelectableArrows {

    public TitanFabricBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> {
            Iterator<Item> iterator = supportedArrows().iterator();
            while (iterator.hasNext()) {
                if (stack.getItem().equals(iterator.next())) {
                    return true;
                }
            }
            return stack.isIn(ItemTags.ARROWS);
        };
    }

    @Environment(EnvType.CLIENT)
    public static void onClientTick(MinecraftClient client) {
        while (KeyBindEvents.BOW_SCREEN_KEY.wasPressed()) {
            if (client.player == null)
                return;
            if (!(client.player.getMainHandStack().getItem() instanceof SelectableArrows bowStack))
                return;

            PlayerInventory inventory = client.player.getInventory();
            List<ItemStack> arrowStacks = inventory.main.stream().filter(itemStack -> bowStack.supportedArrows().contains(itemStack.getItem())).toList();

            client.setScreen(new ArrowSelectionScreen(new LiteralText("Test title"), arrowStacks));
            LoggerUtil.devLogger("opened arrow selection screen");
        }
    }
}
