package net.shirojr.titanfabric.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY;

public class TitanFabricArrowItem extends ArrowItem implements WeaponEffectCrafting {

    public TitanFabricArrowItem(Settings settings) {
        super(settings);
    }

    @Override
    public Optional<ItemType> titanfabric$getCraftingType() {
        return Optional.of(ItemType.PRODUCT);
    }

    @Override
    public List<WeaponEffect> supportedEffects() {
        return Arrays.stream(WeaponEffect.values()).toList();
    }

    @Override
    public Text getName(ItemStack stack) {
        Optional<WeaponEffectData> data = WeaponEffectData.fromNbt(stack.getOrCreateNbt()
                .getCompound(EFFECTS_COMPOUND_NBT_KEY), WeaponEffectType.INNATE_EFFECT);
        if (data.isEmpty() || data.get().weaponEffect() == null) return super.getName(stack);
        return switch (data.get().weaponEffect()) {
            case BLIND -> new TranslatableText("item.titanfabric.blindness_arrow");
            case POISON -> new TranslatableText("item.titanfabric.poison_arrow");
            case WEAK -> new TranslatableText("item.titanfabric.weakness_arrow");
            case WITHER -> new TranslatableText("item.titanfabric.wither_arrow");
            case FIRE -> new TranslatableText("item.titanfabric.fire_arrow");
            default -> {
                LoggerUtil.devLogger("couldn't find weapon effect for arrow", true, null);
                yield new TranslatableText("item.titanfabric.arrow");
            }
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
                case BLIND -> tooltipPrefix = tooltipPrefix + "blindnessArrowEffect";
                case POISON -> tooltipPrefix = tooltipPrefix + "poisonArrowEffect";
                case WEAK -> tooltipPrefix = tooltipPrefix + "weaknessArrowEffect";
                case WITHER -> tooltipPrefix = tooltipPrefix + "witherArrowEffect";
                case FIRE -> tooltipPrefix = tooltipPrefix + "fireArrowEffect";
            }
            tooltip.add(new TranslatableText(tooltipPrefix));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        NbtCompound innateCompound = stack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY)
                .getCompound(WeaponEffectType.INNATE_EFFECT.getNbtKey());
        WeaponEffect weaponEffect = WeaponEffect.getEffect(innateCompound.getString(WeaponEffectData.EFFECT_NBT_KEY));
        int strength = innateCompound.getInt(WeaponEffectData.EFFECTS_STRENGTH_NBT_KEY);
        WeaponEffectData data = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, strength);
        TitanFabricArrowEntity arrowEntity = new TitanFabricArrowEntity(world, shooter, data, stack);
        arrowEntity.initFromStack(stack);
        return arrowEntity;
    }
}
