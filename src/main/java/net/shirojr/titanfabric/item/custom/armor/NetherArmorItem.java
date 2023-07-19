package net.shirojr.titanfabric.item.custom.armor;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.item.custom.material.TitanFabricArmorMaterials;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetherArmorItem extends ArmorItem {
    public NetherArmorItem(EquipmentSlot slot) {
        super(TitanFabricArmorMaterials.NETHER, slot, new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("tooltip.titanfabric.netherArmor"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
