package net.shirojr.titanfabric.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FrostburnCommands implements CommandRegistrationCallback {
    private static final SimpleCommandExceptionType NOT_APPLICABLE =
            new SimpleCommandExceptionType(Text.literal("Only LivingEntity allowed as Target"));

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("frostburn").requires(source -> source.hasPermissionLevel(2))
                .then(literal("force")
                        .then(argument("entity", EntityArgumentType.entity())
                                .then(argument("value", FloatArgumentType.floatArg(0))
                                        .executes(FrostburnCommands::forceFrostburn)
                                )
                        )
                )
                .then(literal("limit")
                        .then(argument("entity", EntityArgumentType.entity())
                                .then(argument("value", FloatArgumentType.floatArg(0))
                                        .executes(FrostburnCommands::setLimitFrostburn)
                                )
                        )
                )
                .then(literal("speed")
                        .then(argument("entity", EntityArgumentType.entity())
                                .then(argument("ticks", IntegerArgumentType.integer(0))
                                        .executes(FrostburnCommands::setFrostburnTickSpeed)
                                )
                        )
                )
        );
    }

    private static int forceFrostburn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity target)) {
            throw NOT_APPLICABLE.create();
        }
        float frostburn = FloatArgumentType.getFloat(context, "value");

        FrostburnComponent targetFrostburnComponent = FrostburnComponent.get(target);
        targetFrostburnComponent.forceFrostburn(frostburn, true);

        return Command.SINGLE_SUCCESS;
    }

    private static int setLimitFrostburn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity target)) {
            throw NOT_APPLICABLE.create();
        }
        float frostburn = FloatArgumentType.getFloat(context, "value");

        FrostburnComponent targetFrostburnComponent = FrostburnComponent.get(target);
        targetFrostburnComponent.setFrostburnLimit(frostburn, true);

        return Command.SINGLE_SUCCESS;
    }

    private static int setFrostburnTickSpeed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity target)) {
            throw NOT_APPLICABLE.create();
        }
        int tickSpeed = IntegerArgumentType.getInteger(context, "ticks");
        FrostburnComponent targetFrostburnComponent = FrostburnComponent.get(target);
        targetFrostburnComponent.setFrostburnTickSpeed(tickSpeed, true);
        return Command.SINGLE_SUCCESS;
    }
}
