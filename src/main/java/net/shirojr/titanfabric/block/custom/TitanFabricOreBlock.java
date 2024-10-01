package net.shirojr.titanfabric.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class TitanFabricOreBlock extends Block {
    public TitanFabricOreBlock(Settings settings, int maxXp) {
        super(settings);
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            int i = 1 + world.random.nextInt(5);
            this.dropExperience(world, pos, i);
        }
    }

}
