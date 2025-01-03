package net.shirojr.titanfabric.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import net.shirojr.titanfabric.item.TitanFabricItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class CitrinStarEntity extends ThrownItemEntity {

    private final List<Integer> changedEffectIds = new ArrayList<Integer>();

    public CitrinStarEntity(World world) {
        super(TitanFabricEntities.CITRIN_STAR, world);
    }

    public CitrinStarEntity(World world, LivingEntity owner) {
        super(TitanFabricEntities.CITRIN_STAR, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return TitanFabricItems.CITRIN_STAR;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient()) {
            if (!this.isRemoved()) {
                this.discard();
            }
        }
    }

    private void updateStatusEffects(LivingEntity livingEntity, StatusEffect statusEffect) {
        if(!world.isClient) {
            if (livingEntity.hasStatusEffect(statusEffect) && !this.changedEffectIds.contains(StatusEffect.getRawId(statusEffect))) {
                StatusEffectInstance oldEffectInstance = livingEntity.getStatusEffect(statusEffect);
                StatusEffectInstanceAccessor oldAccessor = (StatusEffectInstanceAccessor) oldEffectInstance;
                if(oldAccessor != null) {
                    StatusEffectInstance previousEffect = oldAccessor.titanfabric$getPreviousStatusEffect();
                    if (previousEffect != null) {
                        return;
                    }
                }


                int oldDuration = oldEffectInstance.getDuration();
                int newDuration = Math.min(oldDuration, 200); // Use 200 ticks or the old duration if it's less

                StatusEffectInstance newEffectInstance = new StatusEffectInstance(
                        getEffectOpposites().get(statusEffect),
                        newDuration,
                        oldEffectInstance.getAmplifier()
                );

                livingEntity.removeStatusEffect(statusEffect);

                // Only set the previous status effect if the old duration is 10 seconds or more
                if (oldDuration >= 200) {
                    StatusEffectInstanceAccessor accessor = (StatusEffectInstanceAccessor) newEffectInstance;
                    if (accessor != null) {
                        accessor.titanfabric$setPreviousStatusEffect(new StatusEffectInstance(oldEffectInstance));
                        livingEntity.addStatusEffect((StatusEffectInstance) accessor);
                    } else {
                        livingEntity.addStatusEffect(newEffectInstance);
                    }
                } else {
                    // No need to set the previous status effect; it cancels out
                    livingEntity.addStatusEffect(newEffectInstance);
                }

                this.changedEffectIds.add(StatusEffect.getRawId(getEffectOpposites().get(statusEffect)));
            }

        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity && !livingEntity.getWorld().isClient()) {
            for (Map.Entry<StatusEffect, StatusEffect> entry : getEffectOpposites().entrySet()) {
                StatusEffect effect = entry.getKey();
                StatusEffect oppositeEffect = entry.getValue();
                updateStatusEffects(livingEntity, effect);
                updateStatusEffects(livingEntity, oppositeEffect);
            }
            if (livingEntity.isOnFire()) {
                entity.extinguish();
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0));
            } else if (livingEntity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                livingEntity.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
                livingEntity.setOnFireFor(10);
            }
            for (int i = 0; i < 32; ++i) {
                ((ServerWorld) livingEntity.getWorld()).spawnParticles(
                        ParticleTypes.END_ROD,
                        livingEntity.getX(),
                        this.getY(),
                        livingEntity.getZ(),
                        1,            // count
                        this.random.nextGaussian() * 0.3D, this.random.nextGaussian() * 0.3D,
                        this.random.nextGaussian() * 0.3D,          // offsetZ
                        0.0D           // speed
                );
            }

            this.changedEffectIds.clear();
        }
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntity && !entity.isAlive()) {
            this.discard();
        } else {
            super.tick();
        }
    }

    @Override
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        Entity entity = this.getOwner();
        if (entity != null && entity.world.getRegistryKey() != destination.getRegistryKey()) {
            this.setOwner(null);
        }
        return super.moveToWorld(destination);
    }

    private final HashMap<StatusEffect, StatusEffect> getEffectOpposites() {
        final HashMap<StatusEffect, StatusEffect> effectOpposites = new HashMap<StatusEffect, StatusEffect>();
        effectOpposites.put(StatusEffects.BLINDNESS, StatusEffects.NIGHT_VISION);
        effectOpposites.put(StatusEffects.NIGHT_VISION, StatusEffects.BLINDNESS);
        effectOpposites.put(StatusEffects.POISON, StatusEffects.REGENERATION);
        effectOpposites.put(StatusEffects.REGENERATION, StatusEffects.POISON);
        effectOpposites.put(StatusEffects.WEAKNESS, StatusEffects.STRENGTH);
        effectOpposites.put(StatusEffects.STRENGTH, StatusEffects.WEAKNESS);
        effectOpposites.put(StatusEffects.SLOWNESS, StatusEffects.SPEED);
        effectOpposites.put(StatusEffects.SPEED, StatusEffects.SLOWNESS);
        return effectOpposites;
    }

}
