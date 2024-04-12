package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.GameRules;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void titanfabric$dropExtendedInventory(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.isCreative() || player.isSpectator()) return;
        if (player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return;
        LoggerUtil.devLogger("player condition met");
        PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);
        if (persistentPlayerData == null) return;
        Inventory extendedInventory = persistentPlayerData.extraInventory;
        LoggerUtil.devLogger("found inv");
        if (extendedInventory.isEmpty()) return;
        LoggerUtil.devLogger("spawning inventory");
        ItemScatterer.spawn(player.getWorld(), player.getBlockPos(), extendedInventory);
    }
}
