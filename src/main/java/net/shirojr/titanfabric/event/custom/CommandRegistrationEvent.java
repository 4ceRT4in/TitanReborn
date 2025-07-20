package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.titanfabric.command.ExtendedInventoryCommands;

public class CommandRegistrationEvent {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(ExtendedInventoryCommands::register);
    }
}
