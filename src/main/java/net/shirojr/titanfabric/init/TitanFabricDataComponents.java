package net.shirojr.titanfabric.init;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.TitanFabricCodecs;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

public interface TitanFabricDataComponents {
    ComponentType<Boolean> CHARGED = register("charged",
            itemStackBuilder -> itemStackBuilder
                    .codec(Codec.BOOL)
                    .packetCodec(PacketCodecs.BOOL)
    );
    ComponentType<HashSet<WeaponEffectData>> WEAPON_EFFECTS = register("weapon_effects",
            weaponEffectDataBuilder -> weaponEffectDataBuilder
                    .codec(TitanFabricCodecs.WEAPON_EFFECTS)
                    .packetCodec(WeaponEffectData.PACKET_CODEC.collect(PacketCodecs.toCollection(HashSet::new)))
    );


    @SuppressWarnings("SameParameterValue")
    private static <T> ComponentType<T> register(String name, Consumer<ComponentType.Builder<T>> componentTypeConsumer) {
        ComponentType.Builder<T> builder = ComponentType.builder();
        componentTypeConsumer.accept(builder);
        return Registry.register(Registries.DATA_COMPONENT_TYPE, TitanFabric.getId(name), builder.build());
    }

    static void initialize() {
        // static initialisation
    }
}
