package net.shirojr.titanfabric.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import net.shirojr.titanfabric.util.items.Anvilable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(Item.class)
public class ItemMixin {
    @Redirect(method = "isEnchantable", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isDamageable()Z"))
    private boolean titanfabric$enchantmentProductIsEnchantable(Item instance) {
        return instance.isDamageable() || instance instanceof Anvilable;
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void getRarity(ItemStack stack, CallbackInfoReturnable<Rarity> cir) {
        if (stack.hasEnchantments()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if ((entry.getKey() == Enchantments.SHARPNESS && entry.getValue() >= 6)
                        || (entry.getKey() == Enchantments.PROTECTION && entry.getValue() >= 5)
                        || (entry.getKey() == Enchantments.POWER && entry.getValue() >= 6)) {
                    cir.setReturnValue(Rarity.EPIC);
                    break;
                }
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(!world.isClient && stack.getItem() == Items.MILK_BUCKET) {
            ImmunityEffect.resetImmunity(user);
        }
    }

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if(stack.getItem() instanceof ArmorItem) {
            if (stack.getItem() == Items.NETHERITE_HELMET || stack.getItem() == Items.NETHERITE_CHESTPLATE || stack.getItem() == Items.NETHERITE_LEGGINGS || stack.getItem() == Items.NETHERITE_BOOTS) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.netherite_effect"));
            }
            if (ArmorPlatingHelper.hasArmorPlating(stack)) {
                ArmorPlateType plateType = ArmorPlatingHelper.getArmorPlatingType(stack);
                if (plateType != null) {
                    switch (plateType) {
                        case CITRIN:
                            tooltip.add(new TranslatableText("tooltip.titanfabric.citrin_armor_plating_equipped"));
                            break;
                        case DIAMOND:
                            tooltip.add(new TranslatableText("tooltip.titanfabric.diamond_armor_plating_equipped"));
                            break;
                        case NETHERITE:
                            tooltip.add(new TranslatableText("tooltip.titanfabric.netherite_armor_plating_equipped"));
                            break;
                        case LEGEND:
                            tooltip.add(new TranslatableText("tooltip.titanfabric.legend_armor_plating_equipped"));
                            break;
                        case EMBER:
                            tooltip.add(new TranslatableText("tooltip.titanfabric.ember_armor_plating_equipped"));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
