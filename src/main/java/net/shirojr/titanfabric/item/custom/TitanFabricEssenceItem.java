package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanFabricEssenceItem extends Item {
    private final String tooltipTranslationKey;
    public TitanFabricEssenceItem(String tooltipTranslationKey) {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64));
        this.tooltipTranslationKey = tooltipTranslationKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText(this.tooltipTranslationKey));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
