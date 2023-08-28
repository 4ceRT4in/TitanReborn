package net.shirojr.titanfabric.mixin;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow public boolean autoJump = false;
}
