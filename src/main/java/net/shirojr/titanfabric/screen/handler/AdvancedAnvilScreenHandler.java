package net.shirojr.titanfabric.screen.handler;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.shirojr.titanfabric.util.items.Anvilable;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class AdvancedAnvilScreenHandler extends AnvilScreenHandler {
    private int repairItemUsage;
    private String newItemName;
    private final Property levelCost = Property.create();

    public AdvancedAnvilScreenHandler(int syncId, PlayerInventory inventory) {
        super(syncId, inventory);
    }

    public AdvancedAnvilScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(syncId, inventory, context);
        this.addProperty(this.levelCost);
    }

    @Override
    public void updateResult() {
        ItemStack itemStack = this.input.getStack(0);
        this.levelCost.set(1);
        int i = 0;
        int repairCost = 0;
        float xpCostMultiplier = 0.5f;
        int k = 0;
        if (itemStack.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }
        ItemStack itemStack2 = itemStack.copy();
        ItemStack itemStack3 = this.input.getStack(1);
        Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack2);
        repairCost += itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
        this.repairItemUsage = 0;
        if (!itemStack3.isEmpty()) {
            boolean isEnchantedBook = itemStack3.isOf(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantmentNbt(itemStack3).isEmpty();
            if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
                int m;
                int itemStackDamage = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                if (itemStackDamage <= 0) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                for (m = 0; itemStackDamage > 0 && m < itemStack3.getCount(); ++m) {
                    int n = itemStack2.getDamage() - itemStackDamage;
                    itemStack2.setDamage(n);
                    ++i;
                    itemStackDamage = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                }
                this.repairItemUsage = m;
            } else {
                if (!(isEnchantedBook || itemStack2.isOf(itemStack3.getItem()) &&
                        (itemStack2.isDamageable() || itemStack2.getItem() instanceof Anvilable))) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                if (itemStack2.isDamageable() && !isEnchantedBook) {
                    int l = itemStack.getMaxDamage() - itemStack.getDamage();
                    int m = itemStack3.getMaxDamage() - itemStack3.getDamage();
                    int n = m + itemStack2.getMaxDamage() * 12 / 100;
                    int o = l + n;
                    int p = itemStack2.getMaxDamage() - o;
                    if (p < 0) {
                        p = 0;
                    }
                    if (p < itemStack2.getDamage()) {
                        itemStack2.setDamage(p);
                        i += 2;
                    }
                }
                Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
                boolean bl22 = false;
                boolean bl3 = false;
                for (Enchantment enchantment : map2.keySet()) {
                    int r;
                    if (enchantment == null) continue;
                    int enchantmentMap = map.getOrDefault(enchantment, 0);
                    r = enchantmentMap == (r = map2.get(enchantment)) ? r + 1 : Math.max(r, enchantmentMap);
                    boolean bl4 = enchantment.isAcceptableItem(itemStack);
                    if (this.player.getAbilities().creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
                        bl4 = true;
                    }
                    for (Enchantment enchantment2 : map.keySet()) {
                        if (enchantment2 == enchantment || enchantment.canCombine(enchantment2)) continue;
                        bl4 = false;
                        ++i;
                    }
                    if (!bl4) {
                        bl3 = true;
                        continue;
                    }
                    bl22 = true;
                    if (r > enchantment.getMaxLevel()) {
                        r = enchantment.getMaxLevel();
                    }
                    map.put(enchantment, r);
                    int enchantmentCost = 0;
                    switch (enchantment.getRarity()) {
                        case COMMON -> enchantmentCost = 1;
                        case UNCOMMON -> enchantmentCost = 2;
                        case RARE -> enchantmentCost = 4;
                        case VERY_RARE -> enchantmentCost = 8;
                    }
                    if (isEnchantedBook) enchantmentCost = Math.max(1, enchantmentCost / 2);
                    i += enchantmentCost * r;
                    if (itemStack.getCount() <= 1) continue;
                    i = 40;
                }
                if (bl3 && !bl22) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
            }
        }
        if (StringUtils.isBlank(this.newItemName)) {
            if (itemStack.hasCustomName()) {
                k = 1;
                i += k;
                itemStack2.removeCustomName();
            }
        } else if (!this.newItemName.equals(itemStack.getName().getString())) {
            k = 1;
            i += k;
            itemStack2.setCustomName(new LiteralText(this.newItemName));
        }
        this.levelCost.set((int) ((repairCost + i) * xpCostMultiplier));
        if (i <= 0) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (k == i && k > 0 && this.levelCost.get() >= 40) {
            this.levelCost.set(39);
        }
        if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (!itemStack2.isEmpty()) {
            int t = itemStack2.getRepairCost();
            if (!itemStack3.isEmpty() && t < itemStack3.getRepairCost()) {
                t = itemStack3.getRepairCost();
            }
            if (k != i || k == 0) {
                t = AnvilScreenHandler.getNextCost(t);
            }
            itemStack2.setRepairCost(t);
            EnchantmentHelper.set(map, itemStack2);
        }
        this.output.setStack(0, itemStack2);
        this.sendContentUpdates();
    }

}
