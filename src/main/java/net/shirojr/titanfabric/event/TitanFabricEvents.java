package net.shirojr.titanfabric.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.event.custom.*;
import net.shirojr.titanfabric.init.TitanFabricGamerules;

public class TitanFabricEvents {
    public static void registerEvents() {
        CommandRegistrationEvent.register();
        DeathEvents.register();
        ServerEntityEvents.EQUIPMENT_CHANGE.register(new TitanFabricServerEntityEvents.ArmorEvents());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            TitanFabricGamerules.syncArmorHudOverlay(handler.player);
            TitanFabricGamerules.syncDisableSwimming(handler.player);
            ImmunityEffect.syncBlockedEffect(handler.player);
        });
    }

    public static void registerClientEvents() {
        TitanFabricClientTickEvents.register();
        ParachuteFeatureRendererEventHandler.register();
        HudEvent.register();
        ToolTipComponentEvent.register();
    }
}
