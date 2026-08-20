package net.shirojr.titanfabric.entity;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

/** Area cloud created by an Ember Barrel Bomb. */
public class EmberBarrelCloudEntity extends AreaEffectCloudEntity {
    private static final int BURN_DURATION_TICKS = 10 * 20;

    public EmberBarrelCloudEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient()) return;

        double radius = getRadius();
        getWorld().getEntitiesByClass(LivingEntity.class,
                getBoundingBox().expand(radius),
                entity -> entity.squaredDistanceTo(getX(), entity.getY(), getZ()) <= radius * radius)
                .forEach(entity -> entity.setOnFireFor(BURN_DURATION_TICKS / 20));
    }
}
