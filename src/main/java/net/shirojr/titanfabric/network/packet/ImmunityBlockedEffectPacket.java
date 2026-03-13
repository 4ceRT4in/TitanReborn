package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record ImmunityBlockedEffectPacket(UUID entityUuid, Optional<Identifier> blockedEffectId) implements CustomPayload {
    public ImmunityBlockedEffectPacket(UUID entityUuid, @Nullable StatusEffect blockedEffect) {
        this(entityUuid, Optional.ofNullable(blockedEffect).map(Registries.STATUS_EFFECT::getId));
    }

    public static final Id<ImmunityBlockedEffectPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("immunity_blocked_effect_sync"));

    public static final PacketCodec<RegistryByteBuf, ImmunityBlockedEffectPacket> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, ImmunityBlockedEffectPacket::entityUuid,
            PacketCodecs.optional(Identifier.PACKET_CODEC), ImmunityBlockedEffectPacket::blockedEffectId,
            ImmunityBlockedEffectPacket::new
    );

    public void sendPacket(ServerPlayerEntity target) {
        ServerPlayNetworking.send(target, this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public static void handlePacket(ImmunityBlockedEffectPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if (packet.blockedEffectId().isEmpty()) {
                ImmunityEffect.clearBlockedEffect(packet.entityUuid());
                return;
            }
            StatusEffect effect = Registries.STATUS_EFFECT.get(packet.blockedEffectId().get());
            ImmunityEffect.setBlockedEffect(packet.entityUuid(), effect);
        });
    }
}
