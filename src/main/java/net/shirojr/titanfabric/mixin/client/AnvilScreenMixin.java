package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.access.AnvilScreenHandlerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {
    @Unique
    private static final Text REQUIRES_NETHERITE_ANVIL_TEXT = Text.translatable("message.requires_netherite_anvil");
    @Unique
    private static final Text HAS_PLATING_TEXT = Text.translatable("message.has_plating_anvil");

    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Inject(method = "drawForeground", at = @At("TAIL"))
    protected void drawForeground(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        boolean netherite = ((AnvilScreenHandlerAccessor) this.handler).titanfabric$requiresNetherite();
        if (netherite && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
            int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(REQUIRES_NETHERITE_ANVIL_TEXT) - 2;
            context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
            context.drawTextWithShadow(this.textRenderer, REQUIRES_NETHERITE_ANVIL_TEXT, k, 69, 0xFFFFFF);
            return;
        }
        boolean plating = ((AnvilScreenHandlerAccessor) this.handler).titanfabric$hasPlating();
        if (plating && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
            int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(HAS_PLATING_TEXT) - 2;
            context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
            context.drawTextWithShadow(this.textRenderer, HAS_PLATING_TEXT, k, 69, 0xFFFFFF);
        }
    }
}