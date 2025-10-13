package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.handler.NetheriteArmorHandler;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin extends Item {

    public ArmorItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient() && world.getTime() % 5 == 0 && entity instanceof ServerPlayerEntity player && stack.getItem() == Items.NETHERITE_CHESTPLATE
                && ItemStack.areEqual(player.getEquippedStack(EquipmentSlot.CHEST), stack)) {
            int count = ArmorHelper.getNetheriteArmorCount(player);
            if(player.hasStatusEffect(StatusEffects.RESISTANCE)) return;
            if (count == 4) {
                ((NetheriteArmorHandler) player).titanfabric$setFullNetheriteArmor(true);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, -1, 0, false, false, true));
            } else {
                ((NetheriteArmorHandler) player).titanfabric$setFullNetheriteArmor(false);
            }
        }
    }
}
