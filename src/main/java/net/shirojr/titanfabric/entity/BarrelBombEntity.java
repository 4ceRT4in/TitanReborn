package net.shirojr.titanfabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricEntities;
import net.shirojr.titanfabric.init.TitanFabricBlocks;

/** Shared fuse and non-griefing explosion implementation for both barrel variants. */
public class BarrelBombEntity extends TntEntity {
    public enum Type { CITRIN, EMBER }
    private Type type = Type.CITRIN;

    public BarrelBombEntity(EntityType<? extends BarrelBombEntity> entityType, World world) { super(entityType, world); }
    public BarrelBombEntity(World world, double x, double y, double z, LivingEntity owner, Type type) {
        super(TitanFabricEntities.BARREL_BOMB, world);
        refreshPositionAndAngles(x, y, z, 0, 0);
        setFuse(40);
        this.type = type;
        setBlockState(type == Type.CITRIN
                ? TitanFabricBlocks.CITRIN_BARREL_BOMB.getDefaultState()
                : TitanFabricBlocks.EMBER_BARREL_BOMB.getDefaultState());
    }

    @Override public void tick() {
        if (getWorld().isClient) return;
        if (getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    particle(type), getX(), getY() + 0.5, getZ(),
                    3, 0.25, 0.35, 0.25, 0.02
            );
        }
        setFuse(getFuse() - 1);
        if (getFuse() <= 0) explode();
    }

    private void explode() {
        getWorld().createExplosion(this, getX(), getY(), getZ(), 4.0f, false, World.ExplosionSourceType.NONE);
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(getWorld(), getX(), getY(), getZ());
        cloud.setDuration(100); cloud.setRadius(3.0f); cloud.setRadiusOnUse(0); cloud.setRadiusGrowth(0);
        if (type == Type.CITRIN) {
            cloud.setPotionContents(new PotionContentsComponent(net.minecraft.potion.Potions.POISON));
            cloud.addEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0));
        } else {
            cloud.setParticleType(particle(type));
        }
        getWorld().spawnEntity(cloud);
        discard();
    }

    private static ParticleEffect particle(Type type) {
        int color = type == Type.CITRIN ? 0xFF4E9331 : 0xFFFF5A19;
        return EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, color);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("BarrelBombType", type.name());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        try {
            type = Type.valueOf(nbt.getString("BarrelBombType"));
        } catch (IllegalArgumentException ignored) {
            type = Type.CITRIN;
        }
        setBlockState(type == Type.CITRIN
                ? TitanFabricBlocks.CITRIN_BARREL_BOMB.getDefaultState()
                : TitanFabricBlocks.EMBER_BARREL_BOMB.getDefaultState());
    }
}
