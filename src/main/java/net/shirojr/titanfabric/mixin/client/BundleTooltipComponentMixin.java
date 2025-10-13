package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.TitanFabric;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(BundleTooltipComponent.class)
public abstract class BundleTooltipComponentMixin implements TooltipComponent {
    @Shadow @Final
    private BundleContentsComponent bundleContents;

    @Unique
    private static final Identifier BUNDLE_PROGRESS_BAR_BORDER_TEXTURE = TitanFabric.getId("container/bundle/bundle_progressbar_border");
    @Unique
    private static final Identifier BUNDLE_PROGRESS_BAR_FILL_TEXTURE = TitanFabric.getId("container/bundle/bundle_progressbar_fill");
    @Unique
    private static final Identifier BUNDLE_PROGRESS_BAR_FULL_TEXTURE = TitanFabric.getId("container/bundle/bundle_progressbar_full");
    @Unique
    private static final Identifier BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE = TitanFabric.getId("container/bundle/slot_highlight_back");
    @Unique
    private static final Identifier BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE = TitanFabric.getId("container/bundle/slot_highlight_front");
    @Unique
    private static final Identifier BUNDLE_SLOT_BACKGROUND_TEXTURE = TitanFabric.getId("container/bundle/slot_background");
    @Unique
    private static final Text BUNDLE_EMPTY_DESCRIPTION = Text.translatable("item.minecraft.bundle.empty.description");
    @Unique
    private static final Text BUNDLE_FULL = Text.translatable("item.minecraft.bundle.fullness");
    @Unique
    private static final Text BUNDLE_EMPTY = Text.translatable("item.minecraft.bundle");

    public int getHeight() {
        return this.bundleContents.isEmpty() ? 39 : this.getHeightOfNonEmpty();
    }

    public int getWidth(TextRenderer textRenderer) {
        return 96;
    }

    @Unique
    private int getHeightOfNonEmpty() {
        return this.getFixedRowsHeight() + 13 + 8;
    }

    @Unique
    private int getFixedRowsHeight() {
        return this.getRows() * 24;
    }

    @Unique
    private int getRows() {
        return MathHelper.ceilDiv(this.getNumVisibleSlots(), 3);
    }

    @Unique
    private int getNumVisibleSlots() {
        return Math.min(9, this.bundleContents.size());
    }

    @Inject(method = "drawItems", at = @At("HEAD"), cancellable = true)
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context, CallbackInfo ci) {
        if (this.bundleContents.isEmpty()) {
            this.drawEmptyTooltip(textRenderer, x, y, this.getWidth(textRenderer), this.getHeight(), context);
        } else {
            this.drawNonEmptyTooltip(textRenderer, x, y, this.getWidth(textRenderer), this.getHeight(), context);
        }
        ci.cancel();
    }

    @Unique
    private void drawEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        drawEmptyDescription(x + this.getXMargin(width), y, textRenderer, context);
        this.drawProgressBar(x + this.getXMargin(width), y + getDescriptionHeight(textRenderer) + 4, textRenderer, context);
    }

    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
    }

    @Unique
    private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        boolean hasMore = this.bundleContents.size() > 9;
        List<ItemStack> list = this.firstStacksInContents(this.getNumberOfStacksShown());
        int originX = x + this.getXMargin(width);
        int originY = y;
        int k = 1;
        for (int row = 1; row <= this.getRows(); ++row) {
            for (int col = 1; col <= 3; ++col) {
                int n = originX + (col - 1) * 24;
                int o = originY + (row - 1) * 24;
                if (shouldDrawExtraItemsCount(hasMore, col, row)) {
                    drawExtraItemsCount(n, o, this.numContentItemsAfter(list), textRenderer, context);
                } else if (shouldDrawItem(list, k)) {
                    this.drawItem(k, n, o, list, k, textRenderer, context);
                    ++k;
                }
            }
        }
        this.drawProgressBar(x + this.getXMargin(width), y + this.getFixedRowsHeight() + 4, textRenderer, context);
    }

    @Unique
    private static boolean shouldDrawItem(List<ItemStack> items, int itemIndex) {
        return items.size() >= itemIndex;
    }

    @Unique
    private static boolean shouldDrawExtraItemsCount(boolean hasMoreItems, int column, int row) {
        return hasMoreItems && column == 1 && row == 1;
    }

    @Unique
    private List<ItemStack> firstStacksInContents(int numberOfStacksShown) {
        int i = Math.min(this.bundleContents.size(), numberOfStacksShown);
        return this.bundleContents.stream().toList().subList(0, i);
    }

    @Unique
    private static void drawEmptyDescription(int x, int y, TextRenderer textRenderer, DrawContext context) {
        context.drawTextWithBackground(textRenderer, BUNDLE_EMPTY_DESCRIPTION, x, y, 96, 0xAAAAAA);
    }

    @Unique
    private static void drawExtraItemsCount(int x, int y, int numExtra, TextRenderer textRenderer, DrawContext context) {
        context.drawTextWithShadow(textRenderer, "+" + numExtra, x + 12, y + 10, 0xFFFFFF);
    }

    @Unique
    private int getXMargin(int width) {
        return (width - 96) / 2;
    }

    @Unique
    private void drawItem(int index, int x, int y, List<ItemStack> stacks, int seed, TextRenderer textRenderer, DrawContext context) {
        boolean selected = index == 1;
        ItemStack stack = stacks.get(index - 1);
        if (selected) {
            context.drawGuiTexture(BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE, x, y, 24, 24);
        } else {
            context.drawGuiTexture(BUNDLE_SLOT_BACKGROUND_TEXTURE, x, y, 24, 24);
        }
        context.drawItem(stack, x + 4, y + 4, seed);
        context.drawItemInSlot(textRenderer, stack, x + 4, y + 4);
        if (selected) {
            context.drawGuiTexture(BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE, x, y, 24, 24);
        }
    }

    @Unique
    private static int getDescriptionHeight(TextRenderer textRenderer) {
        int lines = textRenderer.wrapLines(BUNDLE_EMPTY_DESCRIPTION, 96).size();
        Objects.requireNonNull(textRenderer);
        return lines * 9;
    }

    @Unique
    private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext context) {
        context.drawGuiTexture(this.getProgressBarFillTexture(), x + 1, y, this.getProgressBarFill(), 13);
        context.drawGuiTexture(BUNDLE_PROGRESS_BAR_BORDER_TEXTURE, x, y, 96, 13);
        Text text = this.getProgressBarLabel();
        if (text != null) {
            context.drawCenteredTextWithShadow(textRenderer, text, x + 48, y + 3, 0xFFFFFF);
        }
    }

    @Unique
    private Identifier getProgressBarFillTexture() {
        return this.bundleContents.getOccupancy().compareTo(Fraction.ONE) >= 0 ? BUNDLE_PROGRESS_BAR_FULL_TEXTURE : BUNDLE_PROGRESS_BAR_FILL_TEXTURE;
    }

    @Unique
    private int getProgressBarFill() {
        return MathHelper.clamp((int)Math.floor(this.bundleContents.getOccupancy().floatValue() * 94.0F), 0, 94);
    }

    @Unique
    private int numContentItemsAfter(List<ItemStack> items) {
        return this.bundleContents.stream().skip(items.size()).mapToInt(ItemStack::getCount).sum();
    }

    @Unique
    private @Nullable Text getProgressBarLabel() {
        if (this.bundleContents.isEmpty()) {
            return BUNDLE_EMPTY;
        } else {
            return this.bundleContents.getOccupancy().compareTo(Fraction.ONE) >= 0 ? BUNDLE_FULL : null;
        }
    }

    @Unique
    public int getNumberOfStacksShown() {
        int size = this.bundleContents.size();
        if (size > 9) return 8;
        return size;
    }
}