package net.shirojr.titanfabric.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import net.shirojr.titanfabric.item.custom.spear.TitanFabricSpearItem;
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

    @Inject(method = "onClickSlot", at = @At("HEAD"), cancellable = true)
    public void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        if (titanfabric$triesToMoveLockedSpear(packet)) {
            markInventoryDirty(player);
            ci.cancel();
            return;
        }
        if (!FireEnchantmentBanHelper.isFireEnchantmentBanEnabled(player.getWorld())) return;

        int slot = packet.getSlot();
        if (slot < 0 || slot >= player.getInventory().size()) return;

        ItemStack stack = player.getInventory().getStack(slot);
        if (stack.isEmpty()) return;

        if (FireEnchantmentBanHelper.stripFireEnchantments(stack)) {
            markInventoryDirty(player);
        }
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void titanfabric$preventThrowingLockedSpear(PlayerActionC2SPacket packet, CallbackInfo ci) {
        boolean dropAttempt = packet.getAction() == PlayerActionC2SPacket.Action.DROP_ITEM
                || packet.getAction() == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS;
        boolean offhandSwap = packet.getAction() == PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND;
        if (!dropAttempt && !offhandSwap) return;

        boolean lockedMainHand = TitanFabricSpearItem.isThrowLocked(player, player.getMainHandStack());
        boolean lockedOffHand = TitanFabricSpearItem.isThrowLocked(player, player.getOffHandStack());
        if (!(lockedMainHand || (offhandSwap && lockedOffHand))) return;
        markInventoryDirty(player);
        ci.cancel();
    }

    @Inject(method = "onCreativeInventoryAction", at = @At("HEAD"), cancellable = true)
    private void titanfabric$preventCreativeRemovalOfLockedSpear(CreativeInventoryActionC2SPacket packet, CallbackInfo ci) {
        int slot = packet.slot();
        boolean replacesLockedSlot = slot >= 1 && slot <= 45
                && TitanFabricSpearItem.isThrowLocked(player, player.playerScreenHandler.getSlot(slot).getStack());
        boolean dropsLockedSpear = slot < 0 && TitanFabricSpearItem.isThrowLocked(player, packet.stack());
        if (!replacesLockedSlot && !dropsLockedSpear) return;
        markInventoryDirty(player);
        ci.cancel();
    }

    @Unique
    private boolean titanfabric$triesToMoveLockedSpear(ClickSlotC2SPacket packet) {
        if (TitanFabricSpearItem.isThrowLocked(player, player.currentScreenHandler.getCursorStack())) return true;
        int slot = packet.getSlot();
        if (slot >= 0 && slot < player.currentScreenHandler.slots.size()
                && TitanFabricSpearItem.isThrowLocked(player, player.currentScreenHandler.getSlot(slot).getStack())) return true;
        if (packet.getActionType() != SlotActionType.SWAP) return false;
        int swapSlot = packet.getButton();
        if (swapSlot >= 0 && swapSlot < 9) {
            return TitanFabricSpearItem.isThrowLocked(player, player.getInventory().getStack(swapSlot));
        }
        return swapSlot == 40 && TitanFabricSpearItem.isThrowLocked(player, player.getOffHandStack());
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
