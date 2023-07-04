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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetherArmorItem extends ArmorItem {
    public NetherArmorItem(EquipmentSlot slot) {
        super(TitanFabricArmorMaterials.NETHER, slot, new FabricItemSettings().group(ItemGroup.COMBAT));
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("tooltip.legendmod.NetherArmor"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}