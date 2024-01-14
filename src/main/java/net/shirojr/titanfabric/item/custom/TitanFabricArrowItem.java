package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.EssenceCrafting;

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
        // TODO: implement new arrow entity for hit effect handling
        WeaponEffects effectId = WeaponEffects.getEffect(stack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));

        TitanFabricArrowEntity arrowEntity = new TitanFabricArrowEntity(world, shooter, effectId, stack);
        arrowEntity.initFromStack(stack);
        return arrowEntity;
    }
}
