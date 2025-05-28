package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabricClient;
import net.shirojr.titanfabric.util.ImageAnimation;


public class S2CNetworking {


    public static void registerClientReceivers() {
        //ClientPlayNetworking.registerGlobalReceiver(NetworkingIdentifiers.ARROW_ENTITY_ITEM_SYNC, S2CNetworking::syncArrowEntityItem);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingIdentifiers.IMAGE_ANIMATION, S2CNetworking::sendImageAnimationPacket);
    }

    private static void sendImageAnimationPacket(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        Identifier identifier = buf.readIdentifier();
        float scale = buf.readFloat();
        long stayTime = buf.readLong();

        client.execute(() -> {
            TitanFabricClient.ANIMATIONS.add(new ImageAnimation(client, identifier, System.currentTimeMillis(), 300L, stayTime, 300L, scale));
        });
    }

    private static void syncArrowEntityItem(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ItemStack newStack = buf.readItemStack();
        int arrowEntityId = buf.readVarInt();
        client.execute(() -> {
            if (client.world == null) return;
            if (!(client.world.getEntityById(arrowEntityId) instanceof PersistentProjectileEntity projectile)) return;

        });
    }
}
