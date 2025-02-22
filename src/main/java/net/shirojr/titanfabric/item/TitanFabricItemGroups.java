package net.shirojr.titanfabric.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.util.ModelPredicateProviders;

public class TitanFabricItemGroups {

    public static final ItemGroup TITAN = FabricItemGroupBuilder.build(new Identifier(TitanFabric.MODID,"titan"),
            () -> new ItemStack(TitanFabricBlocks.LEGEND_CRYSTAL));
}
