package net.shirojr.titanfabric.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricSoundEvents {

    public static final SoundEvent INVINCIBLE_SWOOSH_1 = register("invincible_swoosh_1");
    public static final SoundEvent INVINCIBLE_SWOOSH_2 = register("invincible_swoosh_2");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(TitanFabric.MODID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerModSounds() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod Sounds");
    }
}
