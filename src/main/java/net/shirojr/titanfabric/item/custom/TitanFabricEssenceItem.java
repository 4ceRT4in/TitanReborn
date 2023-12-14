package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.EssenceCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanFabricEssenceItem extends Item implements EssenceCrafting {
    public TitanFabricEssenceItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64));
    }

    @Override
    public Text getName(ItemStack stack) {
        WeaponEffects effect = WeaponEffects.getEffect(stack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
        if (effect == null) return super.getName(stack);
        return switch (effect) {
            case BLIND -> new TranslatableText("item.titanfabric.blindness_essence");
            case FIRE -> new TranslatableText("item.titanfabric.fire_essence");
            case POISON -> new TranslatableText("item.titanfabric.poison_essence");
            case WEAK -> new TranslatableText("item.titanfabric.weakness_essence");
            case WITHER -> new TranslatableText("item.titanfabric.wither_essence");
        };
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (!isIn(group)) return;
        EffectHelper.generateAllEffectVersionStacks(this, stacks);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        WeaponEffects effect = WeaponEffects.getEffect(stack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
        if (effect != null) {
            String tooltipPrefix = "tooltip.titanfabric.";
            switch (effect) {
                case BLIND -> tooltipPrefix = tooltipPrefix + "blindnessEssenceItem";
                case FIRE -> tooltipPrefix = tooltipPrefix + "fireEssenceItem";
                case POISON -> tooltipPrefix = tooltipPrefix + "poisonEssenceItem";
                case WEAK -> tooltipPrefix = tooltipPrefix + "weaknessEssenceItem";
                case WITHER -> tooltipPrefix = tooltipPrefix + "witherEssenceItem";
            }
            tooltip.add(new TranslatableText(tooltipPrefix));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public WeaponEffects ingredientEffect(ItemStack stack) {
        return WeaponEffects.getEffect(stack.getOrCreateNbt());
    }

    @Override
    public ItemType isType() {
        return ItemType.INGREDIENT;
    }
}
