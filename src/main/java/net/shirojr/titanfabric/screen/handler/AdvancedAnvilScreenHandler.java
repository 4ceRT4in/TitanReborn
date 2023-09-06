package net.shirojr.titanfabric.screen.handler;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;
import net.shirojr.titanfabric.init.ConfigInit;

public class AdvancedAnvilScreenHandler extends AnvilScreenHandler {
    private int repairItemUsage;
    private String newItemName;
    private final Property levelCost = Property.create();

    public AdvancedAnvilScreenHandler(int syncId, PlayerInventory inventory) {
        super(syncId, inventory);
    }

    public AdvancedAnvilScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(syncId, inventory, context);
        this.addProperty(this.levelCost);
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            player.addExperienceLevels((int) -(this.levelCost.get() * ConfigInit.CONFIG.anvilXpMultiplier));
        }
        this.input.setStack(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }
        this.levelCost.set(0);
        this.context.run((world, pos) -> {
            BlockState blockState = world.getBlockState((BlockPos)pos);
            if (!player.getAbilities().creativeMode && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12f) {
                BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                if (blockState2 == null) {
                    world.removeBlock((BlockPos)pos, false);
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, (BlockPos)pos, 0);
                } else {
                    world.setBlockState((BlockPos)pos, blockState2, Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, (BlockPos)pos, 0);
                }
            } else {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, (BlockPos)pos, 0);
            }
        });
    }
}
