package net.shirojr.titanfabric.item.custom.armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricArmorMaterials;

import java.util.List;

public class EmberArmorItem extends ArmorItem {
    public EmberArmorItem(Type type, Settings settings) {
        super(TitanFabricArmorMaterials.NETHER.getRegistryEntry(), type,
                settings.maxDamage(TitanFabricArmorMaterials.NETHER.getDurability(type)));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.titanfabric.netherArmor"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
