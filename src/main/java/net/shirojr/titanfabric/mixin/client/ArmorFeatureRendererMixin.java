package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.shirojr.titanfabric.color.GlintContext;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Inject(method = "renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V", at = @At("HEAD"))
    private void beforeRenderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        ItemStack stack = entity.getEquippedStack(armorSlot);
        DyeColor color = null;
        if (ArmorPlatingHelper.hasArmorPlating(stack)) {
            if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.CITRIN)) color = DyeColor.YELLOW;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.LEGEND)) color = DyeColor.CYAN;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.NETHERITE)) color = DyeColor.BLACK;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.EMBER)) color = DyeColor.RED;
            else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.DIAMOND)) color = DyeColor.BLUE;
        }
        if (stack.hasEnchantments()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if ((entry.getKey() == Enchantments.SHARPNESS && entry.getValue() >= 6)
                        || (entry.getKey() == Enchantments.PROTECTION && entry.getValue() >= 5)) {
                    color = DyeColor.ORANGE;
                    break;
                }
            }
        }
        GlintContext.setColor(color);
    }

    @Inject(method = "renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V", at = @At("RETURN"))
    private void afterRenderArmor(CallbackInfo ci) {
        GlintContext.clear();
    }
}