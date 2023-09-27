package net.shirojr.titanfabric.screen;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.screen.handler.DiamondFurnaceScreenHandler;

public class TitanFabricScreenHandlers {
    public static ScreenHandlerType<DiamondFurnaceScreenHandler> DIAMOND_FURNACE_SCREEN_HANDLER =
            registerScreenHandler("diamond_furnace", new ScreenHandlerType<>(DiamondFurnaceScreenHandler::new));

    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_SMALL_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_small", new ScreenHandlerType<>(
                    (syncId, inventory) -> new BackPackItemScreenHandler(syncId, inventory, BackPackItem.Type.SMALL))
            );
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_medium", new ScreenHandlerType<>(
                    (syncId, inventory) -> new BackPackItemScreenHandler(syncId, inventory, BackPackItem.Type.MEDIUM))
            );
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_BIG_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_big", new ScreenHandlerType<>(
                    (syncId, inventory) -> new BackPackItemScreenHandler(syncId, inventory, BackPackItem.Type.BIG))
            );


    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registry.SCREEN_HANDLER, new Identifier(TitanFabric.MODID, name), screenHandlerType);
    }

    public static void register() {

    }
}
