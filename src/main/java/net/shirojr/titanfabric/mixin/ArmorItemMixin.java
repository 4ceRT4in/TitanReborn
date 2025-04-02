package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

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
            int count = ArmorHelper.getNetheriteArmorCount(player);
            if (count == 4) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 209, 0, false, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (stack.getItem() == Items.NETHERITE_HELMET ||
                stack.getItem() == Items.NETHERITE_CHESTPLATE ||
                stack.getItem() == Items.NETHERITE_LEGGINGS ||
                stack.getItem() == Items.NETHERITE_BOOTS) {
            tooltip.add(Text.translatable("tooltip.titanfabric.netherite_effect"));
        }
    }
}
