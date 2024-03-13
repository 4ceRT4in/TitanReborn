package net.shirojr.titanfabric.network;

import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

public class NetworkingIdentifiers {
    public static final Identifier BOW_SCREEN_CHANNEL = new Identifier(TitanFabric.MODID, "bow_screen");
    public static final Identifier MULTI_BOW_ARROWS_CHANNEL = new Identifier(TitanFabric.MODID, "shoot_multi_bow");
    public static final Identifier ARMOR_HANDLING_CHANNEL = new Identifier(TitanFabric.MODID, "armor_handling");
    public static final Identifier ARROW_ENTITY_ITEM_SYNC = new Identifier(TitanFabric.MODID, "arrow_entity_item_sync");
}
