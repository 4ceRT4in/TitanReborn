package net.shirojr.titanfabric.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class TitanFabricSoundHandler {
    private TitanFabricSoundHandler() {

    }

    public static void playParachuteSoundInstance(ClientPlayerEntity player) {
        MinecraftClient.getInstance().getSoundManager().play(new ParachuteSoundInstance(player));
    }
}
