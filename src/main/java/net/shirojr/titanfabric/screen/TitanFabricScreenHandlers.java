package net.shirojr.titanfabric.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

public class TitanFabricScreenHandlers {
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK_ITEM_BIG_SCREEN_HANDLER =
            registerScreenHandler("backpack_item_big", new ExtendedScreenHandlerType<>(BackPackItemScreenHandler::new, BackPackScreenPacket.CODEC));
    public static ScreenHandlerType<ExtendedInventoryScreenHandler> EXTENDED_INVENTORY_SCREEN_HANDLER =
            registerScreenHandler("extended_inventory", new ExtendedScreenHandlerType<>(
                    (syncId, inventory, buf) -> new ExtendedInventoryScreenHandler(syncId, inventory, new SimpleInventory(8), buf)));

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, TitanFabric.getId(name), screenHandlerType);
    }

    public static void register() {

    }
}
