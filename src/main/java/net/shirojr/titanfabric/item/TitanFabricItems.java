package net.shirojr.titanfabric.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricItems {


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(TitanFabric.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TitanFabric.devLogger("Registering " + TitanFabric.MOD_ID + " Mod items");
    }

}
