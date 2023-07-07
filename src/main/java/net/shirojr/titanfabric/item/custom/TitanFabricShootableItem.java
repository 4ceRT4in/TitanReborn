package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;

public class TitanFabricShootableItem extends Item {
    public TitanFabricShootableItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }
}
