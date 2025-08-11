package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.shirojr.titanfabric.color.GlintContext;
import net.shirojr.titanfabric.color.GlintRenderLayer;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ArmorFeatureRendererMixin {

    @WrapOperation(method = "renderGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer renderGlint(Operation<RenderLayer> original) {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.armorEntityGlintColor.get(color.getId());
        }
        return original.call();
    }

    @Inject(method = "renderArmor", at = @At("HEAD"))
    private <T extends LivingEntity, A extends BipedEntityModel<T>> void beforeRenderArmor(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            T entity,
            EquipmentSlot armorSlot,
            int light,
            A model,
            CallbackInfo ci) {

        ItemStack stack = entity.getEquippedStack(armorSlot);
        DyeColor color = null;

        if(OverpoweredEnchantmentsHelper.isOverpowered(stack)) {
            color = DyeColor.ORANGE;
        }

        if (ArmorPlatingHelper.hasArmorPlating(stack)) {
            ArmorPlateType plateType = ArmorPlatingHelper.getArmorPlatingType(stack);
            if (plateType != null) {
                switch (plateType) {
                    case CITRIN -> color = DyeColor.YELLOW;
                    case LEGEND -> color = DyeColor.CYAN;
                    case NETHERITE -> color = DyeColor.BLACK;
                    case EMBER -> color = DyeColor.RED;
                    case DIAMOND -> color = DyeColor.BLUE;
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
