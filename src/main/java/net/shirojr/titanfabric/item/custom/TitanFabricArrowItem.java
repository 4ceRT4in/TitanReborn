package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
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

public class TitanFabricArrowItem extends ArrowItem {
    public TitanFabricArrowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (!isIn(group)) return;
        EffectHelper.generateAllEffectVersionStacks(this, stacks);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        //TODO: implement new arrow entity for hit effect handling
        WeaponEffects effectId = WeaponEffects.getEffect(stack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
        TitanFabricArrowEntity arrowEntity = new TitanFabricArrowEntity(EntityType.ARROW, shooter, world, effectId, stack);
        arrowEntity.initFromStack(stack);
        return arrowEntity;
    }
}
