package net.shirojr.titanfabric.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricItemGroups {
    public static final ItemGroup TITAN = FabricItemGroupBuilder.build(new Identifier(TitanFabric.MODID,"titan"),
            () -> new ItemStack(TitanFabricItems.LEGEND_CRYSTAL));
}
