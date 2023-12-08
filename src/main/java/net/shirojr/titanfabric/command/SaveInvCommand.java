package net.shirojr.titanfabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;

import java.util.ArrayList;
import java.util.List;

public class SaveInvCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("saveinv").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .executes(SaveInvCommand::runSave));
    }

    private static int runSave(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return -1;

        Inventory inventory = player.getInventory();
        List<ItemStack> testStackList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            testStackList.add(inventory.getStack(i));
        }

        MinecraftServer server = context.getSource().getServer();
        PersistentWorldData data = PersistentWorldData.getServerState(server);

        data.players.put(player.getUuid(), new PersistentPlayerData());

        return 0;
    }
}
