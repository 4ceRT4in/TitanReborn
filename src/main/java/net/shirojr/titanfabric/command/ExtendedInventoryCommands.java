package net.shirojr.titanfabric.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExtendedInventoryCommands {
    private static final SimpleCommandExceptionType NOT_ELIGIBLE =
            new SimpleCommandExceptionType(Text.literal("Target doesn't hold Extended Inventories"));
    private static final SimpleCommandExceptionType NOT_A_VIEWER =
            new SimpleCommandExceptionType(Text.literal("Command User can't view Screens"));

    @SuppressWarnings("unused")
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("saveinventory")
                .executes(ExtendedInventoryCommands::viewSelf)
                .then(literal("target")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .then(argument("entity", EntityArgumentType.entity())
                                .executes(ExtendedInventoryCommands::viewOther)
                        )
                )
                .then(literal("global")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .executes(ExtendedInventoryCommands::viewGlobal)
                )
        );
    }

    private static int viewGlobal(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            throw NOT_A_VIEWER.create();
        }
        return view(player, null);
    }

    private static int viewSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            throw NOT_ELIGIBLE.create();
        }
        return view(player, player);
    }

    private static int viewOther(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");

        if (player == null) {
            throw NOT_A_VIEWER.create();
        }
        if (!(entity instanceof LivingEntity target)) {
            throw NOT_ELIGIBLE.create();
        }
        return view(player, target);
    }

    private static int view(ServerPlayerEntity opener, @Nullable LivingEntity target) {
        ExtendedInventoryComponent inventoryComponent;
        if (target == null) {
            inventoryComponent = ExtendedInventoryComponent.getGlobal(opener.getServerWorld());
        } else {
            inventoryComponent = ExtendedInventoryComponent.getTeamOrEntity(target);
        }

        opener.openHandledScreen(new ExtendedScreenHandlerFactory<ExtendedInventoryOpenPacket>() {
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ExtendedInventoryScreenHandler(syncId, playerInventory, inventoryComponent);
            }

            @Override
            public Text getDisplayName() {
                return inventoryComponent.getHeaderText();
            }

            @Override
            public ExtendedInventoryOpenPacket getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
                return new ExtendedInventoryOpenPacket(opener.getId(), Optional.ofNullable(target).map(Entity::getId));
            }
        });
        return Command.SINGLE_SUCCESS;
    }
}
