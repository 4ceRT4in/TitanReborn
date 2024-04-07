package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.DiamondFurnaceScreenHandler;

public class DiamondFurnaceScreen extends AbstractFurnaceScreen<DiamondFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(TitanFabric.MODID, "textures/gui/diamond_furnace.png");

    public DiamondFurnaceScreen(DiamondFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, new FurnaceRecipeBookScreen(), inventory, title, TEXTURE);
    }
}
