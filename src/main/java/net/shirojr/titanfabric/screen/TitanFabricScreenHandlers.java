package net.shirojr.titanfabric.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.screen.handler.DiamondFurnaceScreenHandler;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricScreenHandlers {
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_SMALL_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_small", new ExtendedScreenHandlerType<>(
                    (syncId, inventory, buf) -> new BackPackItemScreenHandler(syncId, inventory, BackPackItem.Type.SMALL, buf))
            );
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_medium", new ExtendedScreenHandlerType<>(
                    (syncId, inventory, buf) -> new BackPackItemScreenHandler(syncId, inventory, BackPackItem.Type.MEDIUM, buf))
            );
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_BIG_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_big", new ExtendedScreenHandlerType<>(
                    (syncId, inventory, buf) -> new BackPackItemScreenHandler(syncId, inventory, BackPackItem.Type.BIG, buf))
            );
    public static ScreenHandlerType<BackPackItemScreenHandler> POTION_BUNDLE_SCREEN_HANDLER =
            registerScreenHandler("potion_bundle", new ExtendedScreenHandlerType<>(
                    (syncId, inventory, buf) -> new BackPackItemScreenHandler(syncId, inventory, BackPackItem.Type.POTION, buf))
            );
    public static ScreenHandlerType<ExtendedInventoryScreenHandler> EXTENDED_INVENTORY_SCREEN_HANDLER =
            registerScreenHandler("extended_inventory", new ExtendedScreenHandlerType<>(
                    (syncId, inventory, buf) -> new ExtendedInventoryScreenHandler(syncId, inventory, new SimpleInventory(8), buf)));
    public static ScreenHandlerType<DiamondFurnaceScreenHandler> DIAMOND_FURNACE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(TitanFabric.MODID, "diamond_furnace"),
            DiamondFurnaceScreenHandler::new
    );;

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registry.SCREEN_HANDLER, new Identifier(TitanFabric.MODID, name), screenHandlerType);
    }

    public static void register() {
        LoggerUtil.devLogger("Initialize ScreenHandlers");
    }
}
