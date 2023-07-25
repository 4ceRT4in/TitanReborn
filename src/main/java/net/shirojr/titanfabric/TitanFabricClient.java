package net.shirojr.titanfabric;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.titanfabric.event.TitanFabricKeyBindEvents;
import net.shirojr.titanfabric.util.ModelPredicateProviders;

public class TitanFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelPredicateProviders.registerAll();
        TitanFabricKeyBindEvents.register();
    }
}
