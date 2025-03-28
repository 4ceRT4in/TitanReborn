package net.shirojr.titanfabric.item.custom.armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricArmorMaterials;

import java.util.List;

public class CitrinArmorItem extends ArmorItem {
    public CitrinArmorItem(Type type, Settings settings) {
        super(TitanFabricArmorMaterials.CITRIN.getRegistryEntry(), type,
                settings.maxDamage(TitanFabricArmorMaterials.CITRIN.getDurability(type)));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.titanfabric.citrinArmor"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
