package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BowItem;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;

public class TitanFabricBowItem extends BowItem {
    public TitanFabricBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }
}
