package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.recipes.PotionBundleContent;

import java.util.List;
import java.util.Optional;

public class PotionBundleItem extends BundleItem {
    private static final String POTION_ITEMS_KEY = "PotionItems";
    private static final int POTION_BAR_COLOR = MathHelper.packRgb(0.6f, 0.2f, 0.8f);

    public PotionBundleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return PotionBundleContent.get(stack).map(content -> content.getOccupancy() > 0).orElse(false);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int occupancy = PotionBundleContent.get(stack).map(PotionBundleContent::getOccupancy).orElse(0);
        return Math.min(1 + 12 * occupancy / PotionBundleContent.MAX_STORAGE, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return POTION_BAR_COLOR;
    }

    @Override
    public boolean onStackClicked(ItemStack bundle, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        }
        ItemStack slotStack = slot.getStack();
        PotionBundleContent content = PotionBundleContent.get(bundle).orElseThrow();

        if (slotStack.isEmpty()) {
            this.playRemoveOneSound(player);
            content.removeFirst().ifPresent(slot::insertStack);
        } else {
            if (!(slotStack.getItem() instanceof PotionItem)) {
                return false;
            }
            if (!content.hasSpace()) {
                return false;
            }
            int added = content.addToContent(slot.takeStackRange(slotStack.getCount(), content.getAvailableSpace(), player));
            if (added > 0) {
                this.playInsertSound(player);
            }
        }
        content.savePersistent(bundle);
        return true;
    }

    @Override
    public boolean onClicked(ItemStack bundle, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return false;
        }
        PotionBundleContent content = PotionBundleContent.get(bundle).orElseThrow();

        if (otherStack.isEmpty()) {
            content.removeFirst().ifPresent(itemStack -> {
                this.playRemoveOneSound(player);
                cursorStackReference.set(itemStack);
            });
        } else {
            if (!(otherStack.getItem() instanceof PotionItem)) {
                return false;
            }
            if (!content.hasSpace()) {
                return false;
            }
            int added = content.addToContent(otherStack);
            if (added > 0) {
                this.playInsertSound(player);
            }
        }
        content.savePersistent(bundle);
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack bundle = user.getStackInHand(hand);
        PotionBundleContent content = PotionBundleContent.get(bundle).orElse(null);
        if (content == null) {
            return TypedActionResult.fail(bundle);
        }
        content.dropContent(user);
        this.playDropContentsSound(user);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(bundle, world.isClient());
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack bundle) {
        PotionBundleContent content = PotionBundleContent.get(bundle).orElse(null);
        if (content == null) return Optional.empty();
        return Optional.of(new PotionBundleContent.ToolTipData(content));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        PotionBundleContent.get(stack).ifPresent(content ->
                tooltip.add(Text.translatable("item.minecraft.bundle.fullness", content.getOccupancy(), PotionBundleContent.MAX_STORAGE).formatted(Formatting.GRAY))
        );
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        PotionBundleContent.get(entity.getStack()).ifPresent(content -> ItemUsage.spawnItemContents(entity, content.stacks()));
    }

    private void playRemoveOneSound(net.minecraft.entity.Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }
}
