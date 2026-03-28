package net.shirojr.titanfabric.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.TitanFabric;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BackPackPreviewData(List<ItemStack> stacks, int occupiedSlots, int totalSlots) {

    public record ToolTipData(BackPackPreviewData previewData) implements TooltipData {
    }

    @Environment(EnvType.CLIENT)
    public record ToolTipComponent(BackPackPreviewData previewData) implements TooltipComponent {
        private static final Identifier PROGRESS_BAR_BORDER_TEXTURE = TitanFabric.getId("container/bundle/bundle_progressbar_border");
        private static final Identifier PROGRESS_BAR_FILL_TEXTURE = TitanFabric.getId("container/bundle/bundle_progressbar_fill");
        private static final Identifier PROGRESS_BAR_FULL_TEXTURE = TitanFabric.getId("container/bundle/bundle_progressbar_full");
        private static final Identifier SLOT_BACKGROUND_TEXTURE = TitanFabric.getId("container/bundle/slot_background");

        @Override
        public int getHeight() {
            return previewData.stacks().isEmpty() ? 39 : getRowsHeight() + 13 + 8;
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            return 96;
        }

        @Override
        public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
            if (previewData.stacks().isEmpty()) {
                drawProgressBar(x, y + 4, textRenderer, context);
                return;
            }
            drawStackGrid(textRenderer, x, y, context);
            drawProgressBar(x, y + getRowsHeight(), textRenderer, context);
        }

        private void drawStackGrid(TextRenderer textRenderer, int x, int y, DrawContext context) {
            List<ItemStack> shownStacks = getShownStacks();
            int startX = x + getItemsXMargin();
            int rowY = y;
            int stackIndex = 0;

            for (int row = 0; row < getRows(); row++) {
                for (int col = 0; col < 3; col++) {
                    if (stackIndex >= shownStacks.size()) break;
                    int slotX = startX + col * 24;
                    context.drawGuiTexture(SLOT_BACKGROUND_TEXTURE, slotX, rowY, 24, 24);
                    ItemStack stack = shownStacks.get(stackIndex);
                    context.drawItem(stack, slotX + 4, rowY + 4, stackIndex);
                    context.drawItemInSlot(textRenderer, stack, slotX + 4, rowY + 4);
                    stackIndex++;
                }
                rowY += 24;
            }
        }

        private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext context) {
            context.drawGuiTexture(getProgressBarFillTexture(), x + 1, y, getProgressBarFill(), 13);
            context.drawGuiTexture(PROGRESS_BAR_BORDER_TEXTURE, x, y, 96, 13);
            Text text = getProgressBarLabel();
            if (text != null) {
                context.drawCenteredTextWithShadow(textRenderer, text, x + 48, y + 3, 0xFFFFFF);
            }
        }

        private Identifier getProgressBarFillTexture() {
            return previewData.occupiedSlots() >= previewData.totalSlots() ? PROGRESS_BAR_FULL_TEXTURE : PROGRESS_BAR_FILL_TEXTURE;
        }

        private int getProgressBarFill() {
            float fillPercent = previewData.totalSlots() <= 0 ? 0.0f : (float) previewData.occupiedSlots() / (float) previewData.totalSlots();
            return MathHelper.clamp((int) Math.floor(fillPercent * 94.0f), 0, 94);
        }

        private @Nullable Text getProgressBarLabel() {
            if (previewData.stacks().isEmpty()) return Text.empty();
            if (previewData.occupiedSlots() >= previewData.totalSlots()) return Text.empty();
            return null;
        }

        private int getRows() {
            return MathHelper.ceilDiv(getNumVisibleSlots(), 3);
        }

        private int getRowsHeight() {
            return getRows() * 24;
        }

        private int getNumVisibleSlots() {
            return Math.min(9, previewData.stacks().size());
        }

        private List<ItemStack> getShownStacks() {
            int visibleSlots = getNumVisibleSlots();
            return previewData.stacks().subList(0, visibleSlots);
        }

        private int getItemsXMargin() {
            return (96 - 72) / 2;
        }
    }
}
