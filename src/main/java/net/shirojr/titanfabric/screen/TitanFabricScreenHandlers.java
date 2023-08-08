package net.shirojr.titanfabric.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.DiamondFurnaceScreenHandler;

public class TitanFabricScreenHandlers {
    public static ScreenHandlerType<DiamondFurnaceScreenHandler> DIAMOND_FURNACE_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        DIAMOND_FURNACE_SCREEN_HANDLER =
                ScreenHandlerRegistry.registerSimple(new Identifier(TitanFabric.MODID, "diamond_furnace"),
                        DiamondFurnaceScreenHandler::new);
    }
}
