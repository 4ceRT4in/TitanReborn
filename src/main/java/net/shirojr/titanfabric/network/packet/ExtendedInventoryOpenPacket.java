package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;
import org.jetbrains.annotations.Nullable;

public record ExtendedInventoryOpenPacket(int playerEntityId, int targetEntityId) implements CustomPayload {
    public static final Id<ExtendedInventoryOpenPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("extended_inventory_open"));

    public static final PacketCodec<RegistryByteBuf, ExtendedInventoryOpenPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ExtendedInventoryOpenPacket::playerEntityId,
            PacketCodecs.VAR_INT, ExtendedInventoryOpenPacket::targetEntityId,
            ExtendedInventoryOpenPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    @Nullable
    public ExtendedInventoryComponent getExtendedInventory(World world) {
        if (!(world.getEntityById(this.targetEntityId) instanceof LivingEntity livingEntity)) return null;
        return ExtendedInventoryComponent.getTeamOrEntity(livingEntity);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getServerWorld();
        if (!(world.getEntityById(this.playerEntityId) instanceof ServerPlayerEntity player)) return;
        ExtendedInventoryComponent extendedInventory = ExtendedInventoryOpenPacket.this.getExtendedInventory(player.getServerWorld());

        player.openHandledScreen(new ExtendedScreenHandlerFactory<ExtendedInventoryOpenPacket>() {
            @Override
            public ExtendedInventoryOpenPacket getScreenOpeningData(ServerPlayerEntity player) {
                return new ExtendedInventoryOpenPacket(
                        ExtendedInventoryOpenPacket.this.playerEntityId,
                        ExtendedInventoryOpenPacket.this.targetEntityId
                );
            }

            @Override
            public Text getDisplayName() {
                if (extendedInventory == null) return Text.empty();
                return extendedInventory.getHeaderText();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                ExtendedInventoryComponent extendedInventory = ExtendedInventoryOpenPacket.this.getExtendedInventory(world);
                return new ExtendedInventoryScreenHandler(syncId, playerInventory, extendedInventory);
            }
        });
    }
}
