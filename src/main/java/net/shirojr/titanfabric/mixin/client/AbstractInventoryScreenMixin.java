package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin<T extends ScreenHandler>
        extends HandledScreen<T> {

    private AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Definition(id = "getAmplifier", method = "Lnet/minecraft/entity/effect/StatusEffectInstance;getAmplifier()I")
    @Definition(id = "statusEffect", local = @Local(type = StatusEffectInstance.class, argsOnly = true))
    @Expression("statusEffect.getAmplifier() >= 1")
    @ModifyExpressionValue(method = "getStatusEffectDescription", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean preventFrostburnEffectAmplifierRendering(boolean original, @Local(argsOnly = true) StatusEffectInstance statusEffectInstance) {
        if (client == null || client.player == null) return original;
        if (!statusEffectInstance.getEffectType().equals(TitanFabricStatusEffects.FROSTBURN)) return original;
        return false;
    }

    @ModifyReturnValue(method = "getStatusEffectDescription", at = @At("RETURN"))
    private Text appendBlockedImmunityText(Text original, StatusEffectInstance effect) {
        if (client == null || client.player == null) return original;
        if (!effect.getEffectType().equals(TitanFabricStatusEffects.IMMUNITY)) {
            return original;
        }
        StatusEffect blocked = ImmunityEffect.getBlockedEffects(client.player.getUuid());
        if (blocked == null) return original;
        return Text.empty()
                .append(original)
                .append(Text.literal(" ("))
                .append(blocked.getName().copy().formatted(Formatting.RED))
                .append(Text.literal(")"));
    }

    @Inject(method = "drawStatusEffectSprites", at = @At("TAIL"))
    private void drawBlockedImmunityIcon(DrawContext context, int x, int height, Iterable<StatusEffectInstance> effects,
                                         boolean wide, CallbackInfo ci) {
        if (client == null || client.player == null) return;

        StatusEffect blocked = ImmunityEffect.getBlockedEffects(client.player.getUuid());
        if (blocked == null) return;

        StatusEffectSpriteManager spriteManager = client.getStatusEffectSpriteManager();
        RegistryEntry<StatusEffect> blockedEntry = Registries.STATUS_EFFECT.getEntry(blocked);
        Sprite blockedSprite = spriteManager.getSprite(blockedEntry);

        int currentY = this.y;
        int iconX = x + (wide ? 6 : 7);
        for (StatusEffectInstance effect : effects) {
            if (effect.getEffectType().equals(TitanFabricStatusEffects.IMMUNITY)) {
                int iconY = currentY + 7;
                context.drawSprite(iconX + 10, iconY + 10, 1, 10, 10, blockedSprite);
                return;
            }
            currentY += height;
        }
    }
}
