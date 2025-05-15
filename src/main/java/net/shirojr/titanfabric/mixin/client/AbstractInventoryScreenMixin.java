package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin<T extends ScreenHandler>
        extends HandledScreen<T> {

    public AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    @Inject(
            method = "drawStatusEffectDescriptions",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;getStatusEffectDescription(Lnet/minecraft/entity/effect/StatusEffectInstance;)Lnet/minecraft/text/Text;",
                    shift = At.Shift.AFTER
            )
    )
    private void drawStatusEffectDescriptions(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects, CallbackInfo ci) {
        int offsetY = this.y;
        for (StatusEffectInstance statusEffectInstance : statusEffects) {
            if (client != null && client.player != null) {
                StatusEffect effect = statusEffectInstance.getEffectType();
                offsetY += height;

                if (effect == TitanFabricStatusEffects.IMMUNITY) {
                    UUID uuid = client.player.getUuid();
                    Set<StatusEffect> blocked = ImmunityEffect.getBlockedEffects(uuid);
                    if (blocked != null && !blocked.isEmpty()) {
                        List<String> lines = blocked.stream()
                                .map(e -> e.getName().getString())
                                .toList();

                        int baseTextX = x + 10 + 18;
                        int effectNameWidth = textRenderer.getWidth(effect.getName());
                        int textX = baseTextX + effectNameWidth + 6;
                        int textY = offsetY - height + 6;

                        float scale = 0.6f;
                        matrices.push();
                        matrices.translate(textX, textY, 0);
                        matrices.scale(scale, scale, 1f);

                        int lineHeight = 9;
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            textRenderer.draw(matrices, line, 0, i * lineHeight, 0xFF5555);
                        }

                        matrices.pop();
                    }
                }
            }
        }
    }
}