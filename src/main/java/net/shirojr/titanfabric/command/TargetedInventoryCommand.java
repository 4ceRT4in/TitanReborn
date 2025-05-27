package net.shirojr.titanfabric.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.data.ExtendedInventory;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

import java.util.UUID;

public class TargetedInventoryCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("inventory")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.literal("player")
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                .executes(context -> handleTargetPlayer(context.getSource().getPlayer(),
                                        EntityArgumentType.getPlayer(context, "target")))))
                .then(CommandManager.literal("uuid")
                        .then(CommandManager.argument("uuid", UuidArgumentType.uuid())
                                .executes(context -> handleTargetUuid(context.getSource().getPlayer(),
                                        UuidArgumentType.getUuid(context, "uuid")))))
        );
    }

    private static int handleTargetPlayer(PlayerEntity user, PlayerEntity target) {
        if (user == null || target == null) return -1;
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(target);
        if (playerData == null) return -1;
        if (target.getDisplayName() != null) {
            openInventory(user, playerData);
        }
        return 1;
    }

    private static int handleTargetUuid(PlayerEntity user, UUID uuid) {
        if (user == null || uuid == null) return -1;
        if (!(user.getWorld() instanceof ServerWorld serverWorld)) return 0;
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(serverWorld, uuid);
        if (playerData == null) return -1;
        openInventory(user, playerData);
        return 1;
    }

    private static void openInventory(PlayerEntity user, PersistentPlayerData playerData) {
        if (!(user.getWorld() instanceof ServerWorld serverWorld)) {
            throw new IllegalStateException("Open inventory command is not possible on client side");
        }
        ExtendedInventoryOpenPacket packet = new ExtendedInventoryOpenPacket(user.getId());
        ExtendedInventory inventory = packet.getExtendedInventory(serverWorld);
        if (inventory == null) return;
        user.openHandledScreen(new ExtendedScreenHandlerFactory<ExtendedInventoryOpenPacket>() {
            @Override
            public ExtendedInventoryOpenPacket getScreenOpeningData(ServerPlayerEntity player) {
                return packet;
            }

            @Override
            public Text getDisplayName() {
                return null;
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory userInventory, PlayerEntity player) {
                return new ExtendedInventoryScreenHandler(syncId, userInventory, inventory);
            }
        });
    }
}
