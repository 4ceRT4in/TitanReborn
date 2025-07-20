package net.shirojr.titanfabric.init;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.data.BackPackContent;
import net.shirojr.titanfabric.data.PotionBundleContent;
import net.shirojr.titanfabric.util.TitanFabricCodecs;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;

import java.util.HashSet;
import java.util.function.Consumer;

public interface TitanFabricDataComponents {
    ComponentType<Boolean> CHARGED = register("charged",
            itemStackBuilder -> itemStackBuilder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    ComponentType<Boolean> ACTIVATED = register("activated",
            builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    ComponentType<Integer> MULTI_BOW_MAX_ARROWS_COUNT = register("max_arrow_count",
            builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT));

    ComponentType<Integer> MULTI_BOW_ARROWS_COUNT = register("arrow_count",
            builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT));

    ComponentType<Integer> MULTI_BOW_PROJECTILE_TICK = register("shooting_tick",
            builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT));

    ComponentType<WeaponEffect> WEAPON_EFFECT = register("weapon_effect",
            builder -> builder.codec(WeaponEffect.CODEC).packetCodec(WeaponEffect.PACKET_CODEC));

    ComponentType<HashSet<WeaponEffectData>> WEAPON_EFFECTS = register("weapon_effects",
            weaponEffectDataBuilder -> weaponEffectDataBuilder
                    .codec(TitanFabricCodecs.WEAPON_EFFECTS)
                    .packetCodec(WeaponEffectData.PACKET_CODEC.collect(PacketCodecs.toCollection(HashSet::new)))
    );

    ComponentType<BackPackContent> BACKPACK_CONTENT = register("backpack_content",
            builder -> builder.codec(BackPackContent.CODEC).packetCodec(BackPackContent.PACKET_CODEC));

    ComponentType<PotionBundleContent> POTION_BUNDLE_CONTENT = register("potion_bundle_content",
            builder -> builder.codec(PotionBundleContent.CODEC).packetCodec(PotionBundleContent.PACKET_CODEC));

    ComponentType<Integer> SELECTED_PROJECTILE = register("selected_projectile",
            builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT));


    ComponentType<ArmorPlateType> ARMOR_PLATING = register("armor_plating",
            builder -> builder.codec(ArmorPlateType.CODEC).packetCodec(ArmorPlateType.PACKET_CODEC));

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
