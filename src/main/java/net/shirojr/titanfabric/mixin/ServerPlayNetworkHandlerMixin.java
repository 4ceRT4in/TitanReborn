package net.shirojr.titanfabric.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.screen.slot.Slot;
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
        if (titanfabric$triesToRemoveLockedSpear(packet)) {
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
        if (!dropAttempt) return;

        boolean lockedMainHand = TitanFabricSpearItem.isThrowLocked(player, player.getMainHandStack());
        if (!lockedMainHand) return;
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
    private boolean titanfabric$triesToRemoveLockedSpear(ClickSlotC2SPacket packet) {
        Slot targetSlot = titanfabric$getClickedSlot(packet.getSlot());
        boolean targetIsPlayerInventory = targetSlot != null && targetSlot.inventory == player.getInventory();
        boolean lockedCursor = TitanFabricSpearItem.isThrowLocked(player, player.currentScreenHandler.getCursorStack());
        boolean lockedTarget = targetSlot != null
                && TitanFabricSpearItem.isThrowLocked(player, targetSlot.getStack());
        boolean lockedTargetInPlayerInventory = lockedTarget && targetIsPlayerInventory;

        return switch (packet.getActionType()) {
            case PICKUP, QUICK_CRAFT -> lockedCursor && !targetIsPlayerInventory;
            case QUICK_MOVE -> lockedTargetInPlayerInventory
                    && player.currentScreenHandler != player.playerScreenHandler;
            case SWAP -> titanfabric$triesToSwapLockedSpear(packet, targetIsPlayerInventory);
            case THROW, CLONE -> lockedCursor || lockedTargetInPlayerInventory;
            case PICKUP_ALL -> false;
        };
    }

    @Unique
    private Slot titanfabric$getClickedSlot(int slot) {
        if (slot < 0 || slot >= player.currentScreenHandler.slots.size()) return null;
        return player.currentScreenHandler.getSlot(slot);
    }

    @Unique
    private boolean titanfabric$triesToSwapLockedSpear(ClickSlotC2SPacket packet, boolean targetIsPlayerInventory) {
        int swapSlot = packet.getButton();
        ItemStack swappedStack = swapSlot >= 0 && swapSlot < 9
                ? player.getInventory().getStack(swapSlot)
                : swapSlot == 40 ? player.getOffHandStack() : ItemStack.EMPTY;
        boolean lockedSwappedStack = TitanFabricSpearItem.isThrowLocked(player, swappedStack);
        return lockedSwappedStack && !targetIsPlayerInventory;
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
