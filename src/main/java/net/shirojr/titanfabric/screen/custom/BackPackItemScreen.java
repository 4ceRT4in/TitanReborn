package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;

public class BackPackItemScreen extends HandledScreen<BackPackItemScreenHandler> {
    private static final Identifier TEXTURE_SMALL = TitanFabric.getId("textures/gui/backpack_small.png");
    private static final Identifier TEXTURE_MEDIUM = TitanFabric.getId("textures/gui/backpack_medium.png");
    private static final Identifier TEXTURE_BIG = TitanFabric.getId("textures/gui/backpack_big.png");
    private static final Identifier TEXTURE_POTION = TitanFabric.getId("textures/gui/potion_bundle.png");
    private static final Identifier TEXTURE_POTION_2 = TitanFabric.getId("textures/gui/potion_bundle_2.png");

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
            case POTION -> {
                long time = client != null && client.world != null ? client.world.getTime() : 0;
                boolean b = (time / 20) % 2 != 0;
                texture = b ? TEXTURE_POTION_2 : TEXTURE_POTION;
            }
            case BIG -> texture = TEXTURE_BIG;
            case MEDIUM -> texture = TEXTURE_MEDIUM;
            default -> texture = TEXTURE_SMALL;
        }
        context.drawTexture(texture, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
