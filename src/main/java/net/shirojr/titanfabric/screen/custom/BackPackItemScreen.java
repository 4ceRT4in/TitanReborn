package net.shirojr.titanfabric.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;

public class BackPackItemScreen extends HandledScreen<BackPackItemScreenHandler> {
    private static final Identifier TEXTURE_SMALL = new Identifier(TitanFabric.MODID, "textures/gui/backpack_small.png");
    private static final Identifier TEXTURE_MEDIUM = new Identifier(TitanFabric.MODID, "textures/gui/backpack_medium.png");
    private static final Identifier TEXTURE_BIG = new Identifier(TitanFabric.MODID, "textures/gui/backpack_big.png");
    private static final Identifier TEXTURE_POTION = new Identifier(TitanFabric.MODID, "textures/gui/potion_bundle.png");
    private static final Identifier TEXTURE_POTION_2 = new Identifier(TitanFabric.MODID, "textures/gui/potion_bundle_2.png");
    public BackPackItemScreen(BackPackItemScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // adjust title position here using titleX, if needed
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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

        RenderSystem.setShaderTexture(0, texture);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
            assert this.client != null;
            if (this.client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
                if (this.client.player != null) {
                    if (this.focusedSlot.hasStack() && this.focusedSlot.getStack() == this.client.player.getMainHandStack()) {
                        return false;
                    }
                    this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                    return true;
                }
            }

            for(int i = 0; i < 9; ++i) {
                if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                    if (this.client.player != null) {
                        if (this.focusedSlot.hasStack() && this.focusedSlot.getStack() == this.client.player.getMainHandStack()) {
                            return false;
                        }
                        this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
