package net.shirojr.titanfabric.mixin;

import java.util.List;
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
    }

}
