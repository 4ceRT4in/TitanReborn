package net.shirojr.titanfabric.mixin;

import java.util.List;

import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.items.ArmorHelper;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin extends Item {

    public ArmorItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient() && world.getTime() % 20 == 0 && entity instanceof ServerPlayerEntity player && stack.getItem() == Items.NETHERITE_CHESTPLATE
                && ItemStack.areEqual(player.getEquippedStack(EquipmentSlot.CHEST), stack)) {
            int count = (int) ArmorHelper.getArmorItems(player).stream().filter(item -> item instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.NETHERITE).count();
            if (count == 4) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 209, 0, false, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.getItem() == Items.NETHERITE_HELMET || stack.getItem() == Items.NETHERITE_CHESTPLATE || stack.getItem() == Items.NETHERITE_LEGGINGS || stack.getItem() == Items.NETHERITE_BOOTS) {
            tooltip.add(new TranslatableText("tooltip.titanfabric.netherite_effect"));
        }
        if(ArmorPlatingHelper.hasArmorPlating(stack)) {
            String s = null;
            String color = "";
            if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.CITRIN)) {
                s = "+2.5% Magic Weapon Protection §7" + ArmorPlatingHelper.getDurability(stack);
                color = "§2";
            } else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.DIAMOND)) {
                s = "+2.5% Diamond Weapon Protection §7" + ArmorPlatingHelper.getDurability(stack);
                color = "§b";
            } else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.NETHERITE)) {
                s = "+2.5% Netherite Weapon Protection §7" + ArmorPlatingHelper.getDurability(stack);
                color = "§8";
            } else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.LEGEND)) {
                s = "+2.5% Titan Weapon Protection §7" + ArmorPlatingHelper.getDurability(stack);
                color = "§3";
            } else if (ArmorPlatingHelper.hasArmorSpecificPlating(stack, ArmorPlateType.EMBER)) {
                s = "+2.5% Ember Weapon Protection §7" + ArmorPlatingHelper.getDurability(stack);
                color = "§c";
            }
            tooltip.add(Text.of(color + s));
        }
    }

}
