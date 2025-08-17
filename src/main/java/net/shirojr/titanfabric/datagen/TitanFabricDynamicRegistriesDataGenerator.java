package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.shirojr.titanfabric.init.TitanFabricDamageTypes;

import java.util.concurrent.CompletableFuture;

public class TitanFabricDynamicRegistriesDataGenerator extends FabricDynamicRegistryProvider {
    public TitanFabricDynamicRegistriesDataGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.CONFIGURED_FEATURE));
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE));
        for (var entry : TitanFabricDamageTypes.ALL_DAMAGE_TYPES.entrySet()) {
            entries.add(registries.getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE), entry.getValue().get());
        }
    }

    @Override
    public String getName() {
        return "Titan Fabric Dynamic Registries Datagen";
    }
}
