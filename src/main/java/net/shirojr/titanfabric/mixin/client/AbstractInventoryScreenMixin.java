package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin<T extends ScreenHandler>
        extends HandledScreen<T> {

    public AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderImmunity(context);
    }

    @Unique
    private void renderImmunity(DrawContext context) {
        if (client != null && client.player != null && !client.player.getStatusEffects().isEmpty()) {
            UUID uuid = client.player.getUuid();
            StatusEffectInstance immunityInstance = client.player.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);
            StatusEffect blocked = ImmunityEffect.getBlockedEffects(uuid);

            if (immunityInstance != null && blocked != null) {
                String blockedName = "Immune: " + blocked.getName().getString();
                int width = textRenderer.getWidth(blockedName);
                int x = this.x + (this.backgroundWidth / 2) - (width / 2);
                int y = this.y - 50;
                context.drawTextWithShadow(textRenderer, blockedName, x, y, 0x55FF55);
            }
        }
    }
}