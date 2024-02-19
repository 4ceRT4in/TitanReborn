package net.shirojr.titanfabric.util.items;

import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;

public class ArmorHelper {

    public static List<Item> getArmorItems(PlayerEntity playerEntity) {
        return IntStream.rangeClosed(0, 3).mapToObj(playerEntity.getInventory()::getArmorStack).map(ItemStack::getItem).toList();
    }

    public static int getNetherArmorCount(PlayerEntity playerEntity) {
        return (int) getArmorItems(playerEntity).stream().filter(item -> item instanceof EmberArmorItem).count();
    }

}
