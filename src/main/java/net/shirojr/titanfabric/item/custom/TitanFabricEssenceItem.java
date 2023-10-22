package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.EssenceCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanFabricEssenceItem extends Item implements EssenceCrafting {
    private final String tooltipTranslationKey;
    private final WeaponEffects effect;
    public TitanFabricEssenceItem(String tooltipTranslationKey, WeaponEffects effect) {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64));
        this.tooltipTranslationKey = tooltipTranslationKey;
        this.effect = effect;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText(this.tooltipTranslationKey));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public WeaponEffects ingredientEffect() {
        return this.effect;
    }

    @Override
    public ItemType isType() {
        return ItemType.INGREDIENT;
    }
}
