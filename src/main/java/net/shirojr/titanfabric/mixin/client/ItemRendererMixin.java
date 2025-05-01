package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.color.GlintContext;
import net.shirojr.titanfabric.color.GlintRenderLayer;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
    private void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        DyeColor color = null;
        if (stack.hasEnchantments()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if ((entry.getKey() == Enchantments.SHARPNESS && entry.getValue() >= 6)
                        || (entry.getKey() == Enchantments.PROTECTION && entry.getValue() >= 5)
                        || (entry.getKey() == Enchantments.POWER && entry.getValue() >= 6)) {
                    color = DyeColor.ORANGE;
                    break;
                }
            }
        }
        if (ArmorPlatingHelper.hasArmorPlating(stack)) {
            if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.CITRIN)) color = DyeColor.YELLOW;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.LEGEND)) color = DyeColor.CYAN;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.NETHERITE)) color = DyeColor.BLACK;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.EMBER)) color = DyeColor.RED;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.DIAMOND)) color = DyeColor.BLUE;
        }
        if (stack.getItem() instanceof EnchantedBookItem) {
            NbtList enchantments = EnchantedBookItem.getEnchantmentNbt(stack);
            for (int i = 0; i < enchantments.size(); i++) {
                NbtCompound ench = enchantments.getCompound(i);
                Identifier id = EnchantmentHelper.getIdFromNbt(ench);
                int level = EnchantmentHelper.getLevelFromNbt(ench);
                if ((id.equals(Registry.ENCHANTMENT.getId(Enchantments.SHARPNESS)) && level >= 6)
                        || (id.equals(Registry.ENCHANTMENT.getId(Enchantments.PROTECTION)) && level >= 5)
                        || (id.equals(Registry.ENCHANTMENT.getId(Enchantments.POWER)) && level >= 6)) {
                    color = DyeColor.ORANGE;
                    break;
                }
            }
        }
        GlintContext.setColor(color);
    }

    @Redirect(method = "getArmorGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectArmorGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.armorGlintColor.get(color.getId());
        }
        return RenderLayer.getArmorGlint();
    }

    @Redirect(method = "getArmorGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectArmorEntityGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.armorEntityGlintColor.get(color.getId());
        }
        return RenderLayer.getArmorEntityGlint();
    }

    @Redirect(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectGetGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.glintColor.get(color.getId());
        }
        return RenderLayer.getGlint();
    }

    @Redirect(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectGetEntityGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.entityGlintColor.get(color.getId());
        }
        return RenderLayer.getEntityGlint();
    }

    @Redirect(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getDirectGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectGetDirectGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.glintDirectColor.get(color.getId());
        }
        return RenderLayer.getDirectGlint();
    }

    @Redirect(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getDirectEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectGetDirectEntityGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.entityGlintDirectColor.get(color.getId());
        }
        return RenderLayer.getDirectEntityGlint();
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("RETURN"))
    private void afterRenderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        GlintContext.clear();
    }
}