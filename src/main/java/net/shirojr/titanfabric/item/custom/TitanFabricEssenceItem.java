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
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY;

public class TitanFabricEssenceItem extends Item implements WeaponEffectCrafting {
    public TitanFabricEssenceItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64));
    }

    @Override
    public Text getName(ItemStack stack) {
        Optional<WeaponEffectData> data = WeaponEffectData.fromNbt(stack.getOrCreateNbt()
                .getCompound(EFFECTS_COMPOUND_NBT_KEY), WeaponEffectType.INNATE_EFFECT);
        if (data.isEmpty() || data.get().weaponEffect() == null) return super.getName(stack);
        return switch (data.get().weaponEffect()) {
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
        EffectHelper.generateAllEffectVersionStacks(this, stacks, true);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Optional<WeaponEffectData> data = WeaponEffectData.fromNbt(stack.getOrCreateNbt()
                .getCompound(EFFECTS_COMPOUND_NBT_KEY), WeaponEffectType.INNATE_EFFECT);
        if (data.isPresent() && data.get().weaponEffect() != null) {
            String tooltipPrefix = "tooltip.titanfabric.";
            switch (data.get().weaponEffect()) {
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
    public WeaponEffect ingredientEffect(ItemStack stack) {
        return WeaponEffect.getEffect(stack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY));
    }

    @Override
    public Optional<ItemType> titanfabric$getCraftingType() {
        return Optional.of(ItemType.INGREDIENT);
    }
}
