package net.shirojr.titanfabric.mixin.client;

import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StatusEffectSpriteManager.class)
public abstract class StatusEffectSpriteManagerMixin {
    private static final Identifier RECOVERY = TitanFabric.getId("recovery");
    private static final Identifier LONG_RECOVERY = TitanFabric.getId("long_recovery");
    private static final Identifier STRONG_RECOVERY = TitanFabric.getId("strong_recovery");

    @ModifyArg(
            method = "getSprite(Lnet/minecraft/registry/entry/RegistryEntry;)Lnet/minecraft/client/texture/Sprite;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/Sprite;"
            ),
            index = 0
    )
    private Identifier titanfabric$shareRecoverySprite(Identifier identifier) {
        return identifier.equals(LONG_RECOVERY) || identifier.equals(STRONG_RECOVERY) ? RECOVERY : identifier;
    }
}
