package net.shirojr.titanfabric.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.*;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.screen.handler.DiamondFurnaceScreenHandler;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

public class TitanFabricScreenHandlers {
    public static ScreenHandlerType<BackPackItemScreenHandler> BACKPACK =
            registerScreenHandler("backpack", new ExtendedScreenHandlerType<>(BackPackItemScreenHandler::new, BackPackScreenPacket.CODEC));
    public static ScreenHandlerType<ExtendedInventoryScreenHandler> EXTENDED_INVENTORY =
            registerScreenHandler("extended_inventory", new ExtendedScreenHandlerType<>(ExtendedInventoryScreenHandler::new, ExtendedInventoryOpenPacket.CODEC));
    public static final ScreenHandlerType<DiamondFurnaceScreenHandler> DIAMOND_FURNACE_SCREEN_HANDLER = registerAbstract("diamond_furnace", DiamondFurnaceScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerAbstract(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, TitanFabric.getId(id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, TitanFabric.getId(name), screenHandlerType);
    }

    public static void initialize() {
        // static initialisation
    }
}
