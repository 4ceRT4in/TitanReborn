package net.shirojr.titanfabric.entity;

import com.google.common.collect.HashBiMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import net.shirojr.titanfabric.init.TitanFabricEntities;
import net.shirojr.titanfabric.init.TitanFabricItems;

import java.util.ArrayList;
import java.util.List;

public class CitrinStarEntity extends ThrownItemEntity {

    private final List<RegistryEntry<StatusEffect>> changedEffects = new ArrayList<>();

    public CitrinStarEntity(EntityType<? extends ThrownItemEntity> type, World world) {
        super(type, world);
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
        if (!this.getWorld().isClient()) {
            if (!this.isRemoved()) {
                this.discard();
            }
        }
    }

    private void updateStatusEffects(LivingEntity livingEntity, RegistryEntry<StatusEffect> statusEffect) {
        if (!getWorld().isClient) {
            boolean unchanged = livingEntity.hasStatusEffect(statusEffect) && !this.changedEffects.contains(statusEffect);
            if (unchanged) {
                StatusEffectInstance oldEffectInstance = livingEntity.getStatusEffect(statusEffect);
                StatusEffectInstanceAccessor oldAccessor = (StatusEffectInstanceAccessor) oldEffectInstance;
                if (oldAccessor != null) {
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

                this.changedEffects.add(getEffectOpposites().get(statusEffect));
            }

        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity && !livingEntity.getWorld().isClient()) {
            for (var entry : getEffectOpposites().entrySet()) {
                RegistryEntry<StatusEffect> effect = entry.getKey();
                RegistryEntry<StatusEffect> oppositeEffect = entry.getValue();
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

            this.changedEffects.clear();
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

    private HashBiMap<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> getEffectOpposites() {
        HashBiMap<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> map = HashBiMap.create();
        map.put(StatusEffects.BLINDNESS, StatusEffects.NIGHT_VISION);
        map.put(StatusEffects.POISON, StatusEffects.REGENERATION);
        map.put(StatusEffects.WEAKNESS, StatusEffects.STRENGTH);
        map.put(StatusEffects.SLOWNESS, StatusEffects.SPEED);
        return map;
    }
}
