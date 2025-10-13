package net.shirojr.titanfabric.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.*;
import net.shirojr.titanfabric.config.TitanConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin
        extends ServerCommonNetworkHandler
        implements ServerPlayPacketListener,
        PlayerAssociatedNetworkHandler,
        TickablePacketListener {


    @Shadow public ServerPlayerEntity player;

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Inject(method = "onClickSlot", at = @At("HEAD"))
    public void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        int slot = packet.getSlot();
        if (slot < 0 || slot >= player.getInventory().size()) return;

        ItemStack stack = player.getInventory().getStack(slot);
        if (stack == null || stack.isEmpty()) return;
        List<String> blockedEnchants = TitanConfig.getBlockedEnchantments();
        if(stack.getItem() instanceof EnchantedBookItem) {
            handleRemoval(stack, blockedEnchants);
        } else {
            handleNormal(stack, blockedEnchants);
        }
    }

    @Unique
    private void handleRemoval(ItemStack stack, List<String> blockedEnchants){
        var stored = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (stored != null) {
            for (var entry : stored.getEnchantmentEntries()) {
                var enchantId = entry.getKey().getKey().get().getValue();
                if (blockedEnchants.contains(enchantId.toString())) {
                    stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
                    player.getInventory().markDirty();
                    player.networkHandler.sendPacket(new InventoryS2CPacket(
                            player.currentScreenHandler.syncId,
                            0,
                            player.currentScreenHandler.getStacks(),
                            player.currentScreenHandler.getCursorStack()
                    ));
                }
            }
        }
    }

    @Unique
    private void handleNormal(ItemStack stack, List<String> blockedEnchants){
        var stored = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (stored != null) {
            for (var entry : stored.getEnchantmentEntries()) {
                var enchantId = entry.getKey().getKey().get().getValue();
                if (blockedEnchants.contains(enchantId.toString())) {
                    stack.remove(DataComponentTypes.ENCHANTMENTS);
                    player.getInventory().markDirty();
                    player.networkHandler.sendPacket(new InventoryS2CPacket(
                            player.currentScreenHandler.syncId,
                            0,
                            player.currentScreenHandler.getStacks(),
                            player.currentScreenHandler.getCursorStack()
                    ));
                }
            }
        }
    }
}
