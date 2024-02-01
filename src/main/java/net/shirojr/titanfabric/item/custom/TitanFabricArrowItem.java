package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.EssenceCrafting;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY;

public class TitanFabricArrowItem extends ArrowItem implements EssenceCrafting {
    private final ArrowSelectionHelper.ArrowType arrowType;

    public TitanFabricArrowItem(ArrowSelectionHelper.ArrowType arrowType) {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
        this.arrowType = arrowType;
    }

    @Override
    public ItemType isType() {
        return ItemType.PRODUCT;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (!isIn(group))
            return;
        EffectHelper.generateAllEffectVersionStacks(this, stacks);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        NbtCompound compound = stack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);
        WeaponEffect weaponEffect = WeaponEffect.getEffect(compound.getString(WeaponEffectData.EFFECT_NBT_KEY));
        int strength = compound.getInt(WeaponEffectData.EFFECTS_STRENGTH_NBT_KEY);
        WeaponEffectData data = new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, weaponEffect, strength);
        TitanFabricArrowEntity arrowEntity = new TitanFabricArrowEntity(world, shooter, data, stack);
        arrowEntity.initFromStack(stack);
        return arrowEntity;
    }
}
