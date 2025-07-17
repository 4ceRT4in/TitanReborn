package net.shirojr.titanfabric.item.custom.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorPlatingItem extends Item {

    private final ArmorPlateType type;

    public ArmorPlatingItem(Settings settings, ArmorPlateType type) {
        super(settings);
        this.type = type;
    }

    public ArmorPlateType getPlateType() {
        return this.type;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        ArmorPlateType plateType = getPlateType();
        if (plateType != null) {
            switch (plateType) {
                case CITRIN:
                    tooltip.add(Text.translatable("tooltip.titanfabric.citrin_armor_plating"));
                    break;
                case DIAMOND:
                    tooltip.add(Text.translatable("tooltip.titanfabric.diamond_armor_plating"));
                    break;
                case NETHERITE:
                    tooltip.add(Text.translatable("tooltip.titanfabric.netherite_armor_plating"));
                    break;
                case LEGEND:
                    tooltip.add(Text.translatable("tooltip.titanfabric.legend_armor_plating"));
                    break;
                case EMBER:
                    tooltip.add(Text.translatable("tooltip.titanfabric.ember_armor_plating"));
                    break;
                default:
                    break;
            }
        }
    }
}
