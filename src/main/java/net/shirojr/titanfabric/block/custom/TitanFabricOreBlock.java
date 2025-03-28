package net.shirojr.titanfabric.block.custom;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class TitanFabricOreBlock extends ExperienceDroppingBlock {
    public TitanFabricOreBlock(AbstractBlock.Settings settings, int minXp, int maxXp) {
        super(UniformIntProvider.create(minXp, maxXp), settings);
    }
}
