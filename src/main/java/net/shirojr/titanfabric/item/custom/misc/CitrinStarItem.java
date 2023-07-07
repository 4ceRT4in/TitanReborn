package net.shirojr.titanfabric.item.custom.misc;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;

public class CitrinStarItem extends Item {
    public CitrinStarItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16));
    }
}
