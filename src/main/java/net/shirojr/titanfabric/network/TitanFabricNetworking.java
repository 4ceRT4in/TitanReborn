package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricNetworking {
    public static final Identifier BOW_SCREEN_CHANNEL = new Identifier(TitanFabric.MODID, "bow_screen");

    public static void handleBowScreenPacket(MinecraftServer server, ServerPlayerEntity player,
                                             ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int slotIndex = buf.readByte();
        int action = buf.readByte();

        server.execute(() -> {
            ItemStack selectedStack = player.getInventory().getStack(slotIndex);
            selectedStack.getOrCreateNbt().putInt("testing_action", action);
        });
    }
}
