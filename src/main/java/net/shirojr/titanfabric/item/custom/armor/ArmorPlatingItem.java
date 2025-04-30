package net.shirojr.titanfabric.item.custom.armor;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ArmorPlateType plateType = getPlateType();
        if (plateType != null) {
            switch (plateType) {
                case CITRIN:
                    tooltip.add(new TranslatableText("tooltip.titanfabric.citrin_armor_plating"));
                    break;
                case DIAMOND:
                    tooltip.add(new TranslatableText("tooltip.titanfabric.diamond_armor_plating"));
                    break;
                case NETHERITE:
                    tooltip.add(new TranslatableText("tooltip.titanfabric.netherite_armor_plating"));
                    break;
                case LEGEND:
                    tooltip.add(new TranslatableText("tooltip.titanfabric.legend_armor_plating"));
                    break;
                case EMBER:
                    tooltip.add(new TranslatableText("tooltip.titanfabric.ember_armor_plating"));
                    break;
                default:
                    break;
            }
        }
    }
}
