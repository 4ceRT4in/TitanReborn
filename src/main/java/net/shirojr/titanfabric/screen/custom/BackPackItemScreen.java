package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;

public class BackPackItemScreen extends HandledScreen<BackPackItemScreenHandler> {
    private static final Identifier TEXTURE_SMALL = TitanFabric.getId("textures/gui/backpack_small.png");
    private static final Identifier TEXTURE_MEDIUM = TitanFabric.getId("textures/gui/backpack_medium.png");
    private static final Identifier TEXTURE_BIG = TitanFabric.getId("textures/gui/backpack_big.png");
    private static final Identifier TEXTURE_POTION = TitanFabric.getId("textures/gui/potion_bundle.png");

    public BackPackItemScreen(BackPackItemScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // adjust title position here using titleX, if needed
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        Identifier texture = TEXTURE_SMALL;

        switch (handler.getBackPackItemType()) {
            case BIG -> texture = TEXTURE_BIG;
            case MEDIUM -> texture = TEXTURE_MEDIUM;
            case POTION -> {
                renderPotionGui(context, x, y, handler.slots);
                return;
            }
        }
        context.drawTexture(texture, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void renderPotionGui(DrawContext context, int x, int y, DefaultedList<Slot> slots) {
        context.drawTexture(TEXTURE_POTION, x, y, 0, 0, 176, 166);
        int firstSlotX = x + 62;
        int firstSlotY = y + 17;
        int slotSize = 18;
        long time = client != null && client.world != null ? client.world.getTime() : 0;
        boolean emptySlotSpriteSwitcher = (time / 20) % 2 != 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = slots.get(i).getStack();
            if (!stack.isEmpty()) continue;

            int slotU = emptySlotSpriteSwitcher ? 176 : 192;
            int slotV = 0;
            context.drawTexture(
                    TEXTURE_POTION,
                    firstSlotX + (i % 3 * slotSize),
                    firstSlotY + (i / 3 * slotSize),
                    slotU, slotV,
                    slotSize, slotSize
            );
        }
    }
}
