package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.shirojr.titanfabric.command.CountdownCommand;
import net.shirojr.titanfabric.command.SaveInvCommand;
import net.shirojr.titanfabric.command.TargetedInventoryCommand;

public class CommandRegistrationEvent {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(SaveInvCommand::register);
        CommandRegistrationCallback.EVENT.register(CountdownCommand::register);
        CommandRegistrationCallback.EVENT.register(TargetedInventoryCommand::register);
    }
}
