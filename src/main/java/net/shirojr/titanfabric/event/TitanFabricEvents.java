package net.shirojr.titanfabric.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.shirojr.titanfabric.event.custom.*;

public class TitanFabricEvents {
    public static void registerEvents() {
        CommandRegistrationEvent.register();
        DeathEvents.register();
        ServerEntityEvents.EQUIPMENT_CHANGE.register(new TitanFabricServerEntityEvents.ArmorEvents());
    }

    public static void registerClientEvents() {
        TitanFabricClientTickEvents.register();
        ParachuteFeatureRendererEventHandler.register();
        HudEvent.register();
        ToolTipComponentEvent.register();
    }
}
