package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.data.server.tag.vanilla.VanillaEnchantmentTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.color.GlintContext;
import net.shirojr.titanfabric.color.GlintRenderLayer;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
    private void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        DyeColor color = null;
        if (stack.hasEnchantments()) {
            ItemEnchantmentsComponent enchantments = stack.getEnchantments();
            for (RegistryEntry<Enchantment> enchantmentEntry : enchantments.getEnchantments()) {
                Optional<RegistryKey<Enchantment>> keyOptional = enchantmentEntry.getKey();
                if (keyOptional.isEmpty()) continue;

                RegistryKey<Enchantment> key = keyOptional.get();
                int level = enchantments.getLevel(enchantmentEntry);

                if ((key == Enchantments.SHARPNESS && level >= 6)
                        || (key == Enchantments.PROTECTION && level >= 5)
                        || (key == Enchantments.POWER && level >= 6)) {
                    color = DyeColor.ORANGE;
                    break;
                }
            }
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

        if (stack.getItem() instanceof EnchantedBookItem) {
            ItemEnchantmentsComponent storedEnchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
            if (storedEnchantments != null) {
                for (RegistryEntry<Enchantment> enchantmentEntry : storedEnchantments.getEnchantments()) {
                    Optional<RegistryKey<Enchantment>> keyOptional = enchantmentEntry.getKey();
                    if (keyOptional.isEmpty()) continue;

                    RegistryKey<Enchantment> key = keyOptional.get();
                    int level = storedEnchantments.getLevel(enchantmentEntry);

                    if ((key == Enchantments.SHARPNESS && level >= 6)
                            || (key == Enchantments.PROTECTION && level >= 5)
                            || (key == Enchantments.POWER && level >= 6)) {
                        color = DyeColor.ORANGE;
                        break;
                    }
                }
            }
        }

        GlintContext.setColor(color);
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

    @Redirect(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectGetDirectGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.glintDirectColor.get(color.getId());
        }
        return RenderLayer.getGlint();
    }

    @Redirect(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getDirectEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer redirectGetDirectEntityGlint() {
        DyeColor color = GlintContext.getColor();
        if (color != null) {
            return GlintRenderLayer.entityGlintDirectColor.get(color.getId());
        }
        return RenderLayer.getDirectEntityGlint();
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("RETURN"))
    private void afterRenderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        GlintContext.clear();
    }
}