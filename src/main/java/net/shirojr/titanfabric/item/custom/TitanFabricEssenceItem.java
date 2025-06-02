package net.shirojr.titanfabric.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.util.VariationHolder;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TitanFabricEssenceItem extends Item implements WeaponEffectCrafting, VariationHolder {
    public TitanFabricEssenceItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.set(TitanFabricDataComponents.WEAPON_EFFECT, WeaponEffect.POISON);
        return stack;
    }

    @Override
    public Text getName(ItemStack stack) {
        Optional<WeaponEffectData> data = WeaponEffectData.get(stack, WeaponEffectType.INNATE_EFFECT);
        if (data.isEmpty() || data.get().weaponEffect() == null) return super.getName(stack);
        return switch (data.get().weaponEffect()) {
            case BLIND -> Text.translatable("item.titanfabric.blindness_essence");
            case FIRE -> Text.translatable("item.titanfabric.fire_essence");
            case POISON -> Text.translatable("item.titanfabric.poison_essence");
            case WEAK -> Text.translatable("item.titanfabric.weakness_essence");
            case WITHER -> Text.translatable("item.titanfabric.wither_essence");
        };
    }

/*    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (!isIn(group)) return;
        EffectHelper.generateAllEffectVersionStacks(this, stacks, true);
    }*/

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Optional<WeaponEffectData> data = WeaponEffectData.get(stack, WeaponEffectType.INNATE_EFFECT);
        if (data.isPresent() && data.get().weaponEffect() != null) {
            String tooltipPrefix = "tooltip.titanfabric.";
            switch (data.get().weaponEffect()) {
                case BLIND -> tooltipPrefix = tooltipPrefix + "blindnessEssenceItem";
                case FIRE -> tooltipPrefix = tooltipPrefix + "fireEssenceItem";
                case POISON -> tooltipPrefix = tooltipPrefix + "poisonEssenceItem";
                case WEAK -> tooltipPrefix = tooltipPrefix + "weaknessEssenceItem";
                case WITHER -> tooltipPrefix = tooltipPrefix + "witherEssenceItem";
            }
            tooltip.add(Text.translatable(tooltipPrefix));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public List<WeaponEffect> supportedEffects() {
        List<WeaponEffect> list = new ArrayList<>(List.of(WeaponEffect.values()));
        list.remove(WeaponEffect.FIRE);
        return list;
    }

    @Override
    public List<ItemStack> getVariations() {
        return EffectHelper.generateEssenceStacks(this, true);
    }
}
