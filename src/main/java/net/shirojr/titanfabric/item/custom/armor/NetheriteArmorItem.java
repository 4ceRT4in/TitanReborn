package net.shirojr.titanfabric.item.custom.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.custom.material.TitanFabricArmorMaterials;

import java.util.List;
import java.util.stream.IntStream;

public class NetheriteArmorItem extends ArmorItem {
    public NetheriteArmorItem(EquipmentSlot slot, Settings settings) {
        super(TitanFabricArmorMaterials.NETHERITE, slot, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!(entity instanceof ServerPlayerEntity player)) return;

        List<Item> armorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();

        boolean fullSet = armorSet.stream().allMatch(item -> item instanceof NetheriteArmorItem);

        if (world.isClient()) return;
        if (fullSet && !player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 0,
                    false, false, true));
        }

        //else if (!fullSet && player.hasStatusEffect(StatusEffects.RESISTANCE)) player.removeStatusEffect(StatusEffects.RESISTANCE);
    }
}
