package net.shirojr.titanfabric.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.shirojr.titanfabric.block.TitanFabricBlockEntities;
import net.shirojr.titanfabric.recipe.TitanFabricRecipies;
import net.shirojr.titanfabric.screen.handler.DiamondFurnaceScreenHandler;

public class DiamondFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    public DiamondFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TitanFabricBlockEntities.DIAMOND_FURNACE, pos, state, TitanFabricRecipies.DIAMOND_FURNACE_RECIPE_TYPE);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.titanfabric.diamond_furnace");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new DiamondFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }


}
