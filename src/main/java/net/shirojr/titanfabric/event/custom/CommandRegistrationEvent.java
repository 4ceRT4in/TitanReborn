package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.titanfabric.command.ExtendedInventoryCommands;
import net.shirojr.titanfabric.command.FrostburnCommands;

public class CommandRegistrationEvent {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(new ExtendedInventoryCommands());
        CommandRegistrationCallback.EVENT.register(new FrostburnCommands());
    }
}

