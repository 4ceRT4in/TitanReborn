package net.shirojr.titanfabric.event;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.shirojr.titanfabric.command.SaveInvCommand;

public class CommandRegistrationEvent {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(SaveInvCommand::register);
    }
}
