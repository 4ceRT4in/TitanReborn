package net.shirojr.titanfabric.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import net.shirojr.titanfabric.util.items.Anvilable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {
    @Redirect(method = "isEnchantable", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z"))
    private boolean titanfabric$enchantmentProductIsEnchantable(ItemStack instance, ComponentType<Integer> componentType) {
        return instance.contains(DataComponentTypes.MAX_DAMAGE) || instance.getItem() instanceof Anvilable;
    }

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if(stack.getItem() instanceof ArmorItem) {
            if (stack.getItem() == Items.NETHERITE_HELMET || stack.getItem() == Items.NETHERITE_CHESTPLATE || stack.getItem() == Items.NETHERITE_LEGGINGS || stack.getItem() == Items.NETHERITE_BOOTS) {
                tooltip.add(Text.translatable("tooltip.titanfabric.netherite_effect"));
            }
            if (stack.getItem() == Items.DIAMOND_HELMET || stack.getItem() == Items.DIAMOND_CHESTPLATE || stack.getItem() == Items.DIAMOND_LEGGINGS || stack.getItem() == Items.DIAMOND_BOOTS) {
                tooltip.add(Text.translatable("tooltip.titanfabric.diamondArmor"));
            }
            if (ArmorPlatingHelper.hasArmorPlating(stack)) {
                ArmorPlateType plateType = ArmorPlatingHelper.getArmorPlatingType(stack);
                if (plateType != null) {
                    switch (plateType) {
                        case CITRIN:
                            tooltip.add(Text.translatable("tooltip.titanfabric.citrin_armor_plating_equipped"));
                            break;
                        case DIAMOND:
                            tooltip.add(Text.translatable("tooltip.titanfabric.diamond_armor_plating_equipped"));
                            break;
                        case NETHERITE:
                            tooltip.add(Text.translatable("tooltip.titanfabric.netherite_armor_plating_equipped"));
                            break;
                        case LEGEND:
                            tooltip.add(Text.translatable("tooltip.titanfabric.legend_armor_plating_equipped"));
                            break;
                        case EMBER:
                            tooltip.add(Text.translatable("tooltip.titanfabric.ember_armor_plating_equipped"));
                            break;
                        default:
                            break;
                    }
                }
            }
        } else if (stack.getItem() instanceof SwordItem) {
            if(stack.getItem() == Items.DIAMOND_SWORD) {
                tooltip.add(Text.translatable("tooltip.titanfabric.diamond_sword"));
            }
        }
    }
}
