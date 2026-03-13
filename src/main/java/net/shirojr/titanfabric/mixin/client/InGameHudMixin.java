package net.shirojr.titanfabric.mixin.client;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.network.packet.ArmorHudOverlayPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Final
    @Shadow
    private MinecraftClient client;


    @Shadow
    @Final
    private static Identifier EFFECT_BACKGROUND_TEXTURE;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        renderArmorPlatingOverlay(context, tickCounter);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("TAIL"))
    private void renderBlockedImmunityEffectIcon(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        var player = client.player;
        if (player == null) return;

        StatusEffect blockedEffect = ImmunityEffect.getBlockedEffects(player.getUuid());
        if (blockedEffect == null) return;

        var effects = player.getStatusEffects();
        if (effects.isEmpty()) return;

        Screen currentScreen = client.currentScreen;
        if (currentScreen instanceof AbstractInventoryScreen<?> inventoryScreen && inventoryScreen.hideStatusEffectHud()) {
            return;
        }

        StatusEffectSpriteManager spriteManager = client.getStatusEffectSpriteManager();
        RegistryEntry<StatusEffect> blockedEffectEntry = Registries.STATUS_EFFECT.getEntry(blockedEffect);
        Sprite blockedEffectSprite = spriteManager.getSprite(blockedEffectEntry);

        int beneficialEffects = 0;
        int harmfulEffects = 0;

        for (StatusEffectInstance effect : Ordering.natural().reverse().sortedCopy(effects)) {
            if (!effect.shouldShowIcon()) continue;

            RegistryEntry<StatusEffect> effectType = effect.getEffectType();
            int x = context.getScaledWindowWidth();
            int y = 1;
            if (client.isDemo()) y += 15;

            if (effectType.value().isBeneficial()) {
                beneficialEffects++;
                x -= 25 * beneficialEffects;
            } else {
                harmfulEffects++;
                x -= 25 * harmfulEffects;
                y += 26;
            }

            if (!effectType.equals(TitanFabricStatusEffects.IMMUNITY)) continue;

            RenderSystem.enableBlend();
            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 300.0F);
            context.drawSprite(x + 13, y + 13, 0, 10, 10, blockedEffectSprite);
            context.getMatrices().pop();
            RenderSystem.disableBlend();
            return;
        }
    }

    @SuppressWarnings("unused")
    @Unique
    private void renderArmorPlatingOverlay(DrawContext context, RenderTickCounter tickCounter) {
        var player = client.player;
        if (player == null) return;
        if (!titanfabric$shouldRenderArmorHudOverlay()) return;

        Screen currentScreen = this.client.currentScreen;
        if (currentScreen instanceof AbstractInventoryScreen) {
            return;
        }

        List<ItemStack> armorToRender = new ArrayList<>();

        Iterator<ItemStack> armorItems = player.getArmorItems().iterator();
        ItemStack[] orderedArmor = new ItemStack[4];

        int idx = 3;
        while (armorItems.hasNext() && idx >= 0) {
            orderedArmor[idx] = armorItems.next();
            idx--;
        }

        for (ItemStack armorItem : orderedArmor) {
            if (armorItem.isEmpty() || !(armorItem.getItem() instanceof ArmorItem)) continue;
            armorToRender.add(armorItem);
        }

        if (armorToRender.isEmpty()) return;

        RenderSystem.enableBlend();

        int y = 1;
        for (ItemStack armorItem : armorToRender) {
            int x = 1;

            context.drawGuiTexture(EFFECT_BACKGROUND_TEXTURE, x, y, 24, 24);

            if (armorItem.isItemBarVisible()) {
                int itemBarStep = armorItem.getItemBarStep();
                int itemBarColor = armorItem.getItemBarColor();

                context.getMatrices().push();
                context.getMatrices().translate(0, 0, 200);

                context.fill(x + 6, y + 17, x + 6 + 13, y + 17 + 2, 0xFF000000);
                context.fill(x + 6, y + 17, x + 6 + itemBarStep, y + 17 + 1, 0xFF000000 | itemBarColor);

                context.getMatrices().pop();
            }

            context.drawItem(armorItem, x + 4, y + 4);

            y += 25;
        }

        RenderSystem.disableBlend();
    }

    @Unique
    private boolean titanfabric$shouldRenderArmorHudOverlay() {
        if (client.getServer() != null) {
            return client.getServer().getGameRules().getBoolean(TitanFabricGamerules.ARMOR_HUD_OVERLAY);
        }
        return ArmorHudOverlayPacket.isClientEnabled();
    }

    /*@Definition(id = "lines", local = @Local(argsOnly = true, ordinal = 2, type = int.class))
    @Expression("? - @(?) * lines")
    @ModifyExpressionValue(method = "renderHealthBar", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int captureYHeartOffset(int original, @Share("n")LocalIntRef n) {
        n.set(original);
        return original;
    }

    @WrapOperation(
            method = "renderHealthBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIZZZ)V",
                    ordinal = 0
            )
    )
    private void applyHeartWobbleOffsetToFrostburnHearts(InGameHud instance, DrawContext context, InGameHud.HeartType type,
                                                         int x, int y, boolean hardcore, boolean blinking, boolean half,
                                                         Operation<Void> original, @Share("n")LocalIntRef n) {
        int sharedValueN = n.get();

        original.call(instance, context, type, x, y, half, blinking, half);
        if (!FrostburnHudRenderer.isInstantiated()) {
            return;
        }
        FrostburnHudRenderer.getInstance().setYWobbleOffset(sharedValueN);
    }*/
}
