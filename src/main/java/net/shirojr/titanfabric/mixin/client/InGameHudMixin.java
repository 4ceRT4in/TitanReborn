package net.shirojr.titanfabric.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.util.HeartsManager;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        renderArmorPlateOverlay(matrices);
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"))
    private void injectCustomHearts(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        HeartsManager.setFrozenHearts(player.hasStatusEffect(TitanFabricStatusEffects.FROSTBURN));
    }

    @Unique
    protected void renderArmorPlateOverlay(MatrixStack matrices) {
        var player = client.player;
        if (player == null) return;
        if (this.client.currentScreen instanceof AbstractInventoryScreen) {
            return;
        }

        List<ItemStack> armorWithPlating = new ArrayList<>();


        Iterator<ItemStack> armorItems = player.getArmorItems().iterator();
        ItemStack[] orderedArmor = new ItemStack[4];

        int idx = 3;
        while (armorItems.hasNext() && idx >= 0) {
            orderedArmor[idx] = armorItems.next();
            idx--;
        }

        for (ItemStack armorItem : orderedArmor) {
            if (armorItem.isEmpty() || !(armorItem.getItem() instanceof ArmorItem)) continue;
            ArmorPlateType plateType = ArmorPlatingHelper.getArmorPlatingType(armorItem);
            if (plateType != null) {
                armorWithPlating.add(armorItem);
            }
        }

        if (armorWithPlating.isEmpty()) return;

        RenderSystem.enableBlend();

        int y = 1;
        for (ItemStack armorItem : armorWithPlating) {
            int x = 1;

            RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexture(matrices, x, y, 141, 166, 24, 24);

            this.client.getItemRenderer().renderGuiItemIcon(armorItem, x + 4, y + 4);

            y += 25;
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
