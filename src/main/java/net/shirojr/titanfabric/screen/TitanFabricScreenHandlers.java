package net.shirojr.titanfabric.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

public class TitanFabricScreenHandlers {
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_big", new ExtendedScreenHandlerType<>(BackPackItemScreenHandler::new, BackPackScreenPacket.CODEC));
    public static ScreenHandlerType<ExtendedInventoryScreenHandler> EXTENDED_INVENTORY_SCREEN_HANDLER =
            registerScreenHandler("extended_inventory", new ExtendedScreenHandlerType<>(ExtendedInventoryScreenHandler::new, ExtendedInventoryOpenPacket.CODEC));

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, TitanFabric.getId(name), screenHandlerType);
    }

    public static void register() {

    }
}
