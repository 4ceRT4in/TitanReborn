package net.shirojr.titanfabric.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin
        extends ServerCommonNetworkHandler
        implements ServerPlayPacketListener,
        PlayerAssociatedNetworkHandler,
        TickablePacketListener {

    @Shadow
    public ServerPlayerEntity player;

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Inject(method = "onClickSlot", at = @At("HEAD"))
    public void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        if (!FireEnchantmentBanHelper.isFireEnchantmentBanEnabled(player.getWorld())) return;

        int slot = packet.getSlot();
        if (slot < 0 || slot >= player.getInventory().size()) return;

        ItemStack stack = player.getInventory().getStack(slot);
        if (stack.isEmpty()) return;

        if (FireEnchantmentBanHelper.stripFireEnchantments(stack)) {
            markInventoryDirty(player);
        }
    }

    @Unique
    private void markInventoryDirty(ServerPlayerEntity player) {
        player.getInventory().markDirty();
        player.networkHandler.sendPacket(new InventoryS2CPacket(
                player.currentScreenHandler.syncId,
                0,
                player.currentScreenHandler.getStacks(),
                player.currentScreenHandler.getCursorStack()
        ));
    }
}
