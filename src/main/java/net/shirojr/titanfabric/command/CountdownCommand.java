package net.shirojr.titanfabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.network.NetworkingIdentifiers;
import net.shirojr.titanfabric.sound.TitanFabricSoundEvents;

public class CountdownCommand {

    private static final Identifier TEXTURE_COUNTER_1 = new Identifier(TitanFabric.MODID, "textures/gui/invincible/counter_1.png");
    private static final Identifier TEXTURE_COUNTER_2 = new Identifier(TitanFabric.MODID, "textures/gui/invincible/counter_2.png");
    private static final Identifier TEXTURE_COUNTER_3 = new Identifier(TitanFabric.MODID, "textures/gui/invincible/counter_3.png");
    private static final Identifier TEXTURE_COUNTER_4 = new Identifier(TitanFabric.MODID, "textures/gui/invincible/counter_4.png");
    private static final Identifier TEXTURE_COUNTER_5 = new Identifier(TitanFabric.MODID, "textures/gui/invincible/counter_5.png");
    private static final Identifier TEXTURE_INVINCIBLE_LOGO = new Identifier(TitanFabric.MODID, "textures/gui/invincible/invincible_logo.png");

    private static CountdownTask activeTask = null;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("countdown")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(CountdownCommand::runCountdown));
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (activeTask != null) {
                activeTask.tick(server);
                if (activeTask.isComplete()) {
                    activeTask = null;
                }
            }
        });
    }

    private static int runCountdown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        if (activeTask == null) {
            activeTask = new CountdownTask(10);
        }

        return 1;
    }

    private static class CountdownTask {
        private int secondsLeft;

        public CountdownTask(int seconds) {
            this.secondsLeft = seconds;
        }

        public void tick(MinecraftServer server) {
            if (server.getTicks() % 20 != 0) return;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.of("Countdown: " + secondsLeft), false);
            }

            if (secondsLeft <= 5 && secondsLeft >= 1) {
                Identifier texture = switch (secondsLeft) {
                    case 5 -> TEXTURE_COUNTER_5;
                    case 4 -> TEXTURE_COUNTER_4;
                    case 3 -> TEXTURE_COUNTER_3;
                    case 2 -> TEXTURE_COUNTER_2;
                    case 1 -> TEXTURE_COUNTER_1;
                    default -> null;
                };

                if (texture != null) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeIdentifier(texture);
                        buf.writeFloat(0.8F);
                        buf.writeLong(600L);
                        ServerPlayNetworking.send(player, NetworkingIdentifiers.IMAGE_ANIMATION, buf);

                        player.playSound(TitanFabricSoundEvents.INVINCIBLE_SWOOSH_1,
                                SoundCategory.PLAYERS,
                                1.0F,
                                1.0F);
                    }
                }
            }
            if(secondsLeft == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeIdentifier(TEXTURE_INVINCIBLE_LOGO);
                    buf.writeFloat(3.0F);
                    buf.writeLong(1200L);
                    ServerPlayNetworking.send(player, NetworkingIdentifiers.IMAGE_ANIMATION, buf);

                    player.playSound(TitanFabricSoundEvents.INVINCIBLE_SWOOSH_2,
                            SoundCategory.PLAYERS,
                            1.0F,
                            1.0F);
                }
            }


            secondsLeft--;
        }

        public boolean isComplete() {
            return secondsLeft < 0;
        }
    }
}