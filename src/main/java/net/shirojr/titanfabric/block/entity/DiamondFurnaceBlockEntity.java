package net.shirojr.titanfabric.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.shirojr.titanfabric.init.TitanFabricBlockEntities;
import net.shirojr.titanfabric.screen.handler.DiamondFurnaceScreenHandler;

public class DiamondFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    public DiamondFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TitanFabricBlockEntities.DIAMOND_FURNACE, pos, state, RecipeType.SMELTING);
        this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }
    @Override
    public int size() {
        return 4;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.titanfabric.diamond_furnace");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new DiamondFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
