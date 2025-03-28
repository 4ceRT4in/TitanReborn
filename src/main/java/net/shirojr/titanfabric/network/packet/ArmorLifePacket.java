package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;

public record ArmorLifePacket(ItemStack oldItem, ItemStack newItem) implements CustomPayload {
    public static final CustomPayload.Id<ArmorLifePacket> IDENTIFIER =
            new CustomPayload.Id<>(TitanFabric.getId("armor_life"));

    public static final PacketCodec<RegistryByteBuf, ArmorLifePacket> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, ArmorLifePacket::oldItem,
            ItemStack.PACKET_CODEC, ArmorLifePacket::newItem,
            ArmorLifePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        // Only handle the case where we're removing Legend armor
        // When equipping new armor, the attribute system handles it automatically
        if (oldItem.getItem() instanceof LegendArmorItem legendArmorItem) {
            ServerPlayerEntity player = context.player();
            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();

            // Force an update by setting health to max if it's above the new max health
            // This fixes the vanilla visual desync
            if (currentHealth > (maxHealth - legendArmorItem.getExtraValue())) {
                player.setHealth(maxHealth - legendArmorItem.getExtraValue());
            }
        }
    }
}
