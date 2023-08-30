package net.shirojr.titanfabric.item.custom.bow;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.CrossbowItem;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.items.SelectableArrows;

public class TitanCrossBowItem extends CrossbowItem implements SelectableArrows {
    public TitanCrossBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }
}
