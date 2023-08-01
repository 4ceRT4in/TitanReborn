package net.shirojr.titanfabric.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;

public class TitanFabricArrowEntity extends ArrowEntity {
    private WeaponEffects effect;
    public TitanFabricArrowEntity(EntityType<? extends ArrowEntity> entityType, Entity owner, World world, WeaponEffects effect) {
        super(entityType, world);
        this.effect = effect;
    }

    @Override
    protected void onHit(LivingEntity target) {
        //TODO: if effect is null -> no effect
        super.onHit(target);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString(EffectHelper.EFFECTS_NBT_KEY, this.effect.getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(EffectHelper.EFFECTS_NBT_KEY)) {
            this.effect = WeaponEffects.getEffect(nbt.getString(EffectHelper.EFFECTS_NBT_KEY));
        }
    }
}
