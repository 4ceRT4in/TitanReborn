package net.shirojr.titanfabric.mixin.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    @Unique
    protected void renderArmorPlateOverlay(MatrixStack matrices) {
        var player = client.player;
        if (player == null) return;
        if (this.client.currentScreen instanceof AbstractInventoryScreen) {
            return;
        }
        Map<ArmorPlateType, Integer> activeArmorPlates = new HashMap<>();

        for (ItemStack armorItem : player.getArmorItems()) {
            if (armorItem.isEmpty() || !(armorItem.getItem() instanceof ArmorItem)) continue;
            ArmorPlateType plateType = ArmorPlatingHelper.getArmorPlate(armorItem);
            if (plateType != null) {
                int durability = ArmorPlatingHelper.getDurability(armorItem);
                activeArmorPlates.put(plateType, durability);
            }
        }

        if (activeArmorPlates.isEmpty()) return;

        RenderSystem.enableBlend();
        int i = 0;

        for (Map.Entry<ArmorPlateType, Integer> entry : activeArmorPlates.entrySet()) {
            ArmorPlateType plateType = entry.getKey();
            int durability = entry.getValue();
            int x = 1;
            int y = 1 + (i * 25);

            RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexture(matrices, x, y, 141, 166, 24, 24);

            Item plateItem = ArmorPlatingHelper.getPlateItem(plateType);

            this.client.getItemRenderer().renderGuiItemIcon(new ItemStack(plateItem), x + 4, y + 4);

            i++;
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }



}
