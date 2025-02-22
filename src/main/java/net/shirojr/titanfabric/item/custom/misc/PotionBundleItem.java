package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PotionBundleItem extends BundleItem {
    private static final String POTION_ITEMS_KEY = "PotionItems";
    public static final int MAX_POTION_STORAGE = 64;
    private static final int POTION_BAR_COLOR = MathHelper.packRgb(0.6f, 0.2f, 0.8f);

    public PotionBundleItem(Item.Settings settings) {
        super(settings);
    }

    public static float getAmountFilled(ItemStack stack) {
        return (float) getPotionBundleOccupancy(stack) / MAX_POTION_STORAGE;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getPotionBundleOccupancy(stack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * getPotionBundleOccupancy(stack) / MAX_POTION_STORAGE, 13);
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
        if (slotStack.isEmpty()) {
            this.playRemoveOneSound(player);
            removeFirstPotion(bundle).ifPresent(removedStack -> slot.insertStack(removedStack));
        } else {
            if (!(slotStack.getItem() instanceof PotionItem)) {
                return false;
            }
            int currentOccupancy = getPotionBundleOccupancy(bundle);
            int availableSpace = MAX_POTION_STORAGE - currentOccupancy;
            if (availableSpace <= 0) {
                return false;
            }
            int added = addToPotionBundle(bundle, slot.takeStackRange(slotStack.getCount(), availableSpace, player));
            if (added > 0) {
                this.playInsertSound(player);
            }
        }
        return true;
    }

    @Override
    public boolean onClicked(ItemStack bundle, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return false;
        }
        if (otherStack.isEmpty()) {
            removeFirstPotion(bundle).ifPresent(itemStack -> {
                this.playRemoveOneSound(player);
                cursorStackReference.set(itemStack);
            });
        } else {
            if (!(otherStack.getItem() instanceof PotionItem)) {
                return false;
            }
            int currentOccupancy = getPotionBundleOccupancy(bundle);
            int availableSpace = MAX_POTION_STORAGE - currentOccupancy;
            if (availableSpace <= 0) {
                return false;
            }
            int added = addToPotionBundle(bundle, otherStack);
            if (added > 0) {
                this.playInsertSound(player);
                otherStack.decrement(added);
            }
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack bundle = user.getStackInHand(hand);
        if (dropAllPotionBundledItems(bundle, user)) {
            this.playDropContentsSound(user);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(bundle, world.isClient());
        }
        return TypedActionResult.fail(bundle);
    }

    private static int addToPotionBundle(ItemStack bundle, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PotionItem)) {
            return 0;
        }
        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains(POTION_ITEMS_KEY)) {
            nbt.put(POTION_ITEMS_KEY, new NbtList());
        }
        int currentOccupancy = getPotionBundleOccupancy(bundle);
        int availableSpace = MAX_POTION_STORAGE - currentOccupancy;
        int toAdd = Math.min(stack.getCount(), availableSpace);
        if (toAdd <= 0) {
            return 0;
        }
        NbtList list = nbt.getList(POTION_ITEMS_KEY, 10);
        Optional<NbtCompound> existingCompound = canMergePotionStack(stack, list);
        if (existingCompound.isPresent()) {
            NbtCompound compound = existingCompound.get();
            ItemStack existingStack = ItemStack.fromNbt(compound);
            existingStack.increment(toAdd);
            NbtCompound newCompound = new NbtCompound();
            existingStack.writeNbt(newCompound);
            list.remove(compound);
            list.add(0, newCompound);
        } else {
            ItemStack copy = stack.copy();
            copy.setCount(toAdd);
            NbtCompound compound = new NbtCompound();
            copy.writeNbt(compound);
            list.add(0, compound);
        }
        return toAdd;
    }

    private static Optional<NbtCompound> canMergePotionStack(ItemStack stack, NbtList list) {
        return list.stream()
                .filter(nbtElement -> nbtElement instanceof NbtCompound)
                .map(nbtElement -> (NbtCompound) nbtElement)
                .filter(compound -> ItemStack.canCombine(ItemStack.fromNbt(compound), stack))
                .findFirst();
    }

    private static int getPotionItemOccupancy(ItemStack stack) {
        return 1;
    }

    private static int getPotionBundleOccupancy(ItemStack bundle) {
        NbtCompound nbt = bundle.getNbt();
        if (nbt == null || !nbt.contains(POTION_ITEMS_KEY)) {
            return 0;
        }
        NbtList list = nbt.getList(POTION_ITEMS_KEY, 10);
        return list.stream()
                .filter(nbtElement -> nbtElement instanceof NbtCompound)
                .mapToInt(nbtElement -> {
                    NbtCompound compound = (NbtCompound) nbtElement;
                    ItemStack stack = ItemStack.fromNbt(compound);
                    return getPotionItemOccupancy(stack) * stack.getCount();
                })
                .sum();
    }

    private static Optional<ItemStack> removeFirstPotion(ItemStack bundle) {
        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains(POTION_ITEMS_KEY)) {
            return Optional.empty();
        }
        NbtList list = nbt.getList(POTION_ITEMS_KEY, 10);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        NbtCompound compound = list.getCompound(0);
        ItemStack stack = ItemStack.fromNbt(compound);
        list.remove(0);
        if (list.isEmpty()) {
            bundle.removeSubNbt(POTION_ITEMS_KEY);
        }
        return Optional.of(stack);
    }

    private static boolean dropAllPotionBundledItems(ItemStack bundle, PlayerEntity player) {
        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains(POTION_ITEMS_KEY)) {
            return false;
        }
        if (player instanceof ServerPlayerEntity) {
            NbtList list = nbt.getList(POTION_ITEMS_KEY, 10);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound compound = list.getCompound(i);
                ItemStack potionStack = ItemStack.fromNbt(compound);
                player.dropItem(potionStack, true);
            }
        }
        bundle.removeSubNbt(POTION_ITEMS_KEY);
        return true;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack bundle) {
        DefaultedList<ItemStack> list = DefaultedList.of();
        getPotionBundledStacks(bundle).forEach(list::add);
        return Optional.of(new BundleTooltipData(list, getPotionBundleOccupancy(bundle)));
    }

    private static Stream<ItemStack> getPotionBundledStacks(ItemStack bundle) {
        NbtCompound nbt = bundle.getNbt();
        if (nbt == null || !nbt.contains(POTION_ITEMS_KEY)) {
            return Stream.empty();
        }
        NbtList list = nbt.getList(POTION_ITEMS_KEY, 10);
        return list.stream()
                .filter(nbtElement -> nbtElement instanceof NbtCompound)
                .map(nbtElement -> (NbtCompound) nbtElement)
                .map(ItemStack::fromNbt);
    }

    @Override
    public void appendTooltip(ItemStack bundle, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.of("§cPrototype Item"));
        tooltip.add(new TranslatableText("item.minecraft.bundle.fullness", getPotionBundleOccupancy(bundle), MAX_POTION_STORAGE).formatted(Formatting.GRAY));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getPotionBundledStacks(entity.getStack()));
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
