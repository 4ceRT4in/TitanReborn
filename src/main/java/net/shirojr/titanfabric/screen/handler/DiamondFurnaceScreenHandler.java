package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;

public class DiamondFurnaceScreenHandler extends AbstractFurnaceScreenHandler {
    public DiamondFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(4));
    }

    public DiamondFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                       PropertyDelegate propertyDelegate) {
        super(TitanFabricScreenHandlers.DIAMOND_FURNACE_SCREEN_HANDLER,
                TitanFabricRecipes.DIAMOND_FURNACE_RECIPE_TYPE, RecipeBookCategory.FURNACE,
                syncId, playerInventory, inventory, propertyDelegate);
    }
}
