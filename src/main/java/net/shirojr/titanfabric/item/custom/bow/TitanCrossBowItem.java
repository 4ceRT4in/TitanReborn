package net.shirojr.titanfabric.item.custom.bow;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;

public class TitanCrossBowItem extends CrossbowItem {
    public TitanCrossBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }
}
