package net.shirojr.titanfabric.item.custom.bow;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.List;

public class TitanCrossBowItem extends CrossbowItem implements SelectableArrows {
    public static final String CHARGED_KEY = TitanFabric.MODID + ".charged";

    public TitanCrossBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1).maxDamage(-1));
    }

    @Override
    public List<Item> supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW);
    }
}
