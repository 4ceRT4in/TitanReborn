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
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderImmunity(matrices);
    }

    @Unique
    private void renderImmunity(MatrixStack matrices) {
        if (client != null && client.player != null && client.player.getStatusEffects().size() > 1) {
            UUID uuid = client.player.getUuid();
            StatusEffectInstance immunityInstance = client.player.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);
            StatusEffect blocked = ImmunityEffect.getBlockedEffects(uuid);

            if (immunityInstance != null && blocked != null) {
                String blockedName = blocked.getName().getString();
                int width = textRenderer.getWidth(blockedName);
                int x = this.x + (this.backgroundWidth / 2) - (width / 2);
                int y = this.y - 50;
                textRenderer.draw(matrices, blockedName, x, y, 0xFF5555);
            }
        }
    }

    @Inject(
            method = "drawStatusEffectDescriptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;getStatusEffectDescription(Lnet/minecraft/entity/effect/StatusEffectInstance;)Lnet/minecraft/text/Text;"
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
                    StatusEffect blocked = ImmunityEffect.getBlockedEffects(uuid);

                    if (blocked != null) {
                        String blockedName = blocked.getName().getString();

                        int baseTextX = x + 10 + 18;
                        int effectNameWidth = textRenderer.getWidth(effect.getName());
                        int textX = baseTextX + effectNameWidth + 6;
                        int textY = offsetY - height + 6;

                        float scale = 0.6f;
                        matrices.push();
                        matrices.translate(textX, textY, 0);
                        matrices.scale(scale, scale, 1f);

                        textRenderer.draw(matrices, blockedName, 0, 0, 0xFF5555);

                        matrices.pop();
                    }
                }
            }
        }
    }
}