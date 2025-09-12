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
import net.minecraft.util.Formatting;
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
                                .executes(FrostburnCommands::printFrostburnSpeed)
                                .then(argument("ticks", IntegerArgumentType.integer(0))
                                        .then(argument("phase", FrostburnComponent.Phase.PhaseArgumentType.phase())
                                                .executes(FrostburnCommands::setFrostburnTickSpeed)
                                        )
                                )
                        )
                )
                .then(literal("print")
                        .then(argument("entity", EntityArgumentType.entity())
                                .executes(FrostburnCommands::print)
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

        context.getSource().sendFeedback(() -> Text.literal("Forced Frostburn to " + frostburn), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int setLimitFrostburn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity target)) {
            throw NOT_APPLICABLE.create();
        }
        float frostburn = FloatArgumentType.getFloat(context, "value");

        FrostburnComponent targetFrostburnComponent = FrostburnComponent.get(target);
        targetFrostburnComponent.setFrostburnLimit(frostburn, true);

        context.getSource().sendFeedback(() -> Text.literal("Set Frostburn Target Limit to " + frostburn), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int setFrostburnTickSpeed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity target)) {
            throw NOT_APPLICABLE.create();
        }
        int tickSpeed = IntegerArgumentType.getInteger(context, "ticks");
        FrostburnComponent.Phase phase = FrostburnComponent.Phase.PhaseArgumentType.getPhase(context, "phase");
        FrostburnComponent targetFrostburnComponent = FrostburnComponent.get(target);
        switch (phase) {
            case INCREASE -> targetFrostburnComponent.setFrostburnTickSpeedIncrease(tickSpeed);
            case DECREASE -> targetFrostburnComponent.setFrostburnTickSpeedDecrease(tickSpeed);
        }

        context.getSource().sendFeedback(() -> Text.literal("Set Frostburn Tick Speed to " + tickSpeed), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int printFrostburnSpeed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity target)) {
            throw NOT_APPLICABLE.create();
        }
        FrostburnComponent frostburnComponent = FrostburnComponent.get(target);
        context.getSource().sendFeedback(
                () -> Text.literal(target.getNameForScoreboard()).append(Text.literal(": %s increase ticks".formatted(frostburnComponent.getFrostburnTickSpeedIncrease()))),
                true
        );
        context.getSource().sendFeedback(
                () -> Text.literal(target.getNameForScoreboard()).append(Text.literal(": %s decrease ticks".formatted(frostburnComponent.getFrostburnTickSpeedDecrease()))),
                true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int print(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity target)) {
            throw NOT_APPLICABLE.create();
        }
        FrostburnComponent frostburnComponent = FrostburnComponent.get(target);

        context.getSource().sendFeedback(() -> Text.literal("[%s]:".formatted(target.getNameForScoreboard())).formatted(Formatting.BLUE), true);
        context.getSource().sendFeedback(() -> Text.literal("Frostburn: " + frostburnComponent.getFrostburn()), true);
        context.getSource().sendFeedback(() -> Text.literal("Frostburn Target Limit: " + frostburnComponent.getFrostburnLimit()), true);
        context.getSource().sendFeedback(() -> Text.literal("Frostburn phase: " + frostburnComponent.getPhase()), true);
        context.getSource().sendFeedback(() -> Text.literal("Maintains or increases Frostburn: " + frostburnComponent.shouldMaintainFrostburn()), true);
        return Command.SINGLE_SUCCESS;
    }
}
