package net.shirojr.titanfabric.util.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.*;

public class ProjectileStackSorter {
    // using ":" to avoid potential collisions with item IDs
    public static final String IS_IN_BAG_KEY = ":stack:is:from:bag:";

    private final PlayerEntity player;
    private List<ItemStack> stacks;

    @SafeVarargs
    public ProjectileStackSorter(PlayerEntity player, Collection<ItemStack>... toBeProcessed) {
        this.player = player;
        this.stacks = new ArrayList<>();
        for (Collection<ItemStack> rawCollection : toBeProcessed) {
            stacks.addAll(rawCollection);
        }
    }

    public List<ItemStack> getResult() {
        return new ArrayList<>(this.stacks);
    }

    public ProjectileStackSorter sortItemType() {
        this.stacks.sort(Comparator.comparingInt(ProjectileStackSorter::getItemTypePriority));
        return this;
    }

    public ProjectileStackSorter sortStackCount() {
        this.stacks.sort(Comparator.comparingInt(ItemStack::getCount).reversed());
        return this;
    }

    public ProjectileStackSorter sortIsInBag() {
        this.stacks.sort((stack1, stack2) -> {
            boolean isFromBag1 = SelectableArrow.Index.get(this.player, stack1)
                    .map(SelectableArrow.Index::isInBag)
                    .orElse(false);
            boolean isFromBag2 = SelectableArrow.Index.get(this.player, stack2)
                    .map(SelectableArrow.Index::isInBag)
                    .orElse(false);
            return Boolean.compare(isFromBag2, isFromBag1);
        });
        return this;
    }

    public ProjectileStackSorter keepUniqueBestStacks() {
        Map<Set<String>, ItemStack> bestStacks = new HashMap<>(this.stacks.size());

        for (ItemStack stack : this.stacks) {
            boolean isFromBag = SelectableArrow.Index.get(this.player, stack)
                    .map(SelectableArrow.Index::isInBag)
                    .orElse(false);
            HashSet<String> uniqueNameHolder = createUniqueNameHolder(stack, isFromBag);

            bestStacks.compute(uniqueNameHolder, (key, existingStack) -> {
                if (existingStack == null) return stack;

                boolean existingIsFromBag = SelectableArrow.Index.get(this.player, existingStack)
                        .map(SelectableArrow.Index::isInBag)
                        .orElse(false);
                if (!existingIsFromBag && isFromBag) return stack;
                if (existingIsFromBag && !isFromBag) return existingStack;

                if (existingStack.getCount() >= stack.getCount()) return existingStack;
                else return stack;
            });
        }

        // could probably be done in the same step as adding entries, but its 3 AM right now... idc
        bestStacks.entrySet().removeIf(entry -> {
            Set<String> uniqueNameHolder = new HashSet<>(entry.getKey());
            if (uniqueNameHolder.contains(IS_IN_BAG_KEY)) return false;
            uniqueNameHolder.add(IS_IN_BAG_KEY);
            return bestStacks.containsKey(uniqueNameHolder);
        });

        this.stacks = new ArrayList<>(bestStacks.values());
        return this;
    }

    private static int getItemTypePriority(ItemStack stack) {
        if (WeaponEffectData.get(stack, WeaponEffectType.INNATE_EFFECT).isPresent()) return 100;
        Item item = stack.getItem();
        if (item instanceof TitanFabricArrowItem) return 200;
        if (item instanceof PotionItem) {
            if (item instanceof SplashPotionItem) return 300;
            if (item instanceof LingeringPotionItem) return 301;
            return 302;
        } else return 400;
    }

    private HashSet<String> createUniqueNameHolder(ItemStack stack, Boolean isFromBag) {
        Item item = stack.getItem();
        HashSet<String> uniqueNameHolder = new HashSet<>();
        uniqueNameHolder.add(item.toString());

        if (isFromBag) {
            uniqueNameHolder.add(IS_IN_BAG_KEY);
        }

        if (item instanceof PotionItem) {
            PotionContentsComponent effectComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (effectComponent != null) {
                for (StatusEffectInstance effect : effectComponent.getEffects()) {
                    uniqueNameHolder.add(effect.getEffectType().getIdAsString());
                }
            }
        } else if (item instanceof TitanFabricArrowItem) {
            WeaponEffectData.get(stack, WeaponEffectType.INNATE_EFFECT).ifPresent(data ->
                    uniqueNameHolder.add(data.weaponEffect().name())
            );
        }
        return uniqueNameHolder;
    }
}