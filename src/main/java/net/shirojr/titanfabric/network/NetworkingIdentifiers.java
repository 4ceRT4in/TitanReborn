package net.shirojr.titanfabric.network;

import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

public class NetworkingIdentifiers {
    public static final Identifier BOW_SCREEN_CHANNEL =TitanFabric.getId("bow_screen");
    public static final Identifier ARMOR_HANDLING_CHANNEL = TitanFabric.getId("armor_handling");
    public static final Identifier ARROW_ENTITY_ITEM_SYNC = TitanFabric.getId("arrow_entity_item_sync");
    public static final Identifier EXTENDED_INVENTORY_OPEN = TitanFabric.getId("extended_inventory_open");
    public static final Identifier ARROW_SELECTION = TitanFabric.getId("arrow_selection");
}
