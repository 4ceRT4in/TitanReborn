package net.shirojr.titanfabric.data;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record PotionBundleContent(List<ItemStack> stacks) {
    public static final int MAX_STORAGE = 64;
    public static final PotionBundleContent DEFAULT = new PotionBundleContent(List.of());

    private static final int potionOccupancySize = 1;

    public static final PacketCodec<RegistryByteBuf, PotionBundleContent> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), PotionBundleContent::stacks,
            PotionBundleContent::new
    );

    public static final Codec<PotionBundleContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("stacks").forGetter(PotionBundleContent::stacks)
    ).apply(instance, PotionBundleContent::new));

    public ItemStack get(int index) {
        return this.stacks.get(index);
    }

    public Stream<ItemStack> stream() {
        return this.stacks.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> iterate() {
        return this.stacks;
    }

    public Iterable<ItemStack> iterateCopy() {
        return Lists.transform(this.stacks, ItemStack::copy);
    }

    public void clear() {
        this.stacks.clear();
    }

    public int size() {
        return this.stacks.size();
    }

    public List<ItemStack> nonEmptySlots() {
        return stream().filter(stack -> !stack.isEmpty()).toList();
    }

    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PotionBundleContent component)) return false;
        for (int i = 0; i < component.size(); i++) {
            ItemStack left = this.stacks.get(i);
            ItemStack right = component.stacks.get(i);
            if (!ItemStack.areEqual(left, right)) return false;
        }
        return true;
    }

    public static Optional<PotionBundleContent> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(TitanFabricDataComponents.POTION_BUNDLE_CONTENT));
    }

    public int getOccupancy() {
        int occupancy = 0;
        for (ItemStack stack : this.stacks) {
            occupancy += potionOccupancySize * stack.getCount();
        }
        return occupancy;
    }

    public boolean hasNoSpace() {
        return getAvailableSpace() <= 0;
    }

    public int getAvailableSpace() {
        return PotionBundleContent.MAX_STORAGE - this.getOccupancy();
    }

    public void savePersistent(ItemStack bundleStack) {
        if (!bundleStack.contains(TitanFabricDataComponents.POTION_BUNDLE_CONTENT)) {
            throw new NullPointerException("Bundle Stack has no Bundle Content data");
        }
        bundleStack.set(TitanFabricDataComponents.POTION_BUNDLE_CONTENT, this);
    }

    // ------------------- [ util ] -------------------

    /**
     * Adds potion stack to potion bundle content and decrements original stack accordingly
     *
     * @param potionStack potion stack which will be inserted
     * @return amount of successfully inserted items
     * @apiNote make sure to call {@link #savePersistent(ItemStack)} to apply the changes!
     */
    public int addToContent(ItemStack potionStack) {
        int addedAmount = 0;
        if (potionStack.isEmpty() || !(potionStack.getItem() instanceof PotionItem) || hasNoSpace()) {
            return addedAmount;
        }
        addedAmount = Math.min(potionStack.getCount(), getAvailableSpace());
        if (addedAmount < 1) {
            return addedAmount;
        }

        for (int i = 0; i < this.stacks.size(); i++) {
            ItemStack entry = this.stacks.get(i);
            int transferCount = Math.min(potionStack.getCount(), potionStack.getMaxCount() - entry.getCount());
            if (entry.isEmpty()) {
                this.stacks.set(i, potionStack.split(transferCount));
                break;
            }
            if (ItemStack.areItemsAndComponentsEqual(potionStack, entry)) {
                potionStack.decrement(transferCount);
                entry.increment(transferCount);
                break;
            }
        }
        return addedAmount;
    }

    /**
     * Removes the first entry of the potion bundle content
     *
     * @return a copy of the removed stack
     * @apiNote make sure to call {@link #savePersistent(ItemStack)} to apply the changes!
     */
    public Optional<ItemStack> removeFirst() {
        if (iterate().iterator().hasNext()) {
            ItemStack removedStack = iterate().iterator().next().copy();
            iterate().iterator().remove();
            return Optional.of(removedStack);
        }
        return Optional.empty();
    }

    /**
     * Clears potion bundle content. If a player is specified, the items will be dropped with their
     * retained ownership.
     *
     * @param player use null if only the content needs to be cleared without dropping the ItemStacks from the player
     * @apiNote make sure to call {@link #savePersistent(ItemStack)} to apply the changes!
     */
    public void dropContent(@Nullable PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            for (ItemStack stack : this.stacks) {
                player.dropItem(stack, true);
            }
        }
        this.clear();
    }

    public record ToolTipData(PotionBundleContent content) implements TooltipData {

    }
    
    public record ToolTipComponent(PotionBundleContent content) implements TooltipComponent {
        private static final Identifier BACKGROUND_TEXTURE = TitanFabric.getId("container/potion_bundle/background");

        @Override
        public int getHeight() {
            return this.getRowsHeight() + 4;
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            return this.getColumnsWidth();
        }

        private int getColumnsWidth() {
            return this.getColumns() * 18 + 2;
        }

        private int getRowsHeight() {
            return this.getRows() * 20 + 2;
        }

        private int getColumns() {
            return Math.max(2, (int)Math.ceil(Math.sqrt(content.size() + 1.0)));
        }

        private int getRows() {
            return (int)Math.ceil((content.size() + 1.0) / this.getColumns());
        }

        @Override
        public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
            int i = this.getColumns();
            int j = this.getRows();
            context.drawGuiTexture(BACKGROUND_TEXTURE, x, y, this.getColumnsWidth(), this.getRowsHeight());
            boolean isEmpty = this.content.getOccupancy() < 1;
            int k = 0;

            for (int l = 0; l < j; l++) {
                for (int m = 0; m < i; m++) {
                    int n = x + m * 18 + 1;
                    int o = y + l * 20 + 1;
                    this.drawSlot(n, o, k++, !isEmpty, context, textRenderer);
                }
            }
        }

        private void drawSlot(int x, int y, int index, boolean shouldBlock, DrawContext context, TextRenderer textRenderer) {
            if (index >= this.content.size()) {
                this.draw(context, x, y, shouldBlock ? SlotSprite.BLOCKED_SLOT : SlotSprite.SLOT);
            } else {
                ItemStack itemStack = this.content.get(index);
                this.draw(context, x, y, SlotSprite.SLOT);
                context.drawItem(itemStack, x + 1, y + 1, index);
                context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
                if (index == 0) {
                    HandledScreen.drawSlotHighlight(context, x + 1, y + 1, 0);
                }
            }
        }

        private void draw(DrawContext context, int x, int y, SlotSprite sprite) {
            context.drawGuiTexture(sprite.texture, x, y, 0, sprite.width, sprite.height);
        }

        @Environment(EnvType.CLIENT)
        enum SlotSprite {
            BLOCKED_SLOT(TitanFabric.getId("container/potion_bundle/blocked_slot"), 18, 20),
            SLOT(TitanFabric.getId("container/potion_bundle/slot"), 18, 20);

            public final Identifier texture;
            public final int width;
            public final int height;

            private SlotSprite(final Identifier texture, final int width, final int height) {
                this.texture = texture;
                this.width = width;
                this.height = height;
            }
        }
    }
}
