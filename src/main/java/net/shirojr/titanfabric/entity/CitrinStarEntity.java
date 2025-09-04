package net.shirojr.titanfabric.entity;

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
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import net.shirojr.titanfabric.init.TitanFabricEntities;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.BiDirectionalLookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                RegistryEntry<StatusEffect> oppositeEffect = getEffectOpposites().getOpposite(statusEffect);
                if (oppositeEffect == null) return;

                StatusEffectInstance oldEffectInstance = livingEntity.getStatusEffect(statusEffect);
                if (!(oldEffectInstance instanceof StatusEffectInstanceAccessor accessor)) return;
                StatusEffectInstance previousEffect = accessor.titanfabric$getPreviousStatusEffect();
                if (previousEffect != null) {
                    return;
                }

                int oldDuration = oldEffectInstance.getDuration();
                int newDuration = Math.min(oldDuration, 200); // Use 200 ticks or the old duration if it's less

                StatusEffectInstance newEffectInstance = new StatusEffectInstance(
                        oppositeEffect,
                        newDuration,
                        oldEffectInstance.getAmplifier()
                );

                livingEntity.removeStatusEffect(statusEffect);

                // Only set the previous status effect if the old duration is 10 seconds or more
                if (oldDuration >= 200) {
                    StatusEffectInstanceAccessor newAccessor = (StatusEffectInstanceAccessor) newEffectInstance;
                    if (newAccessor != null) {
                        newAccessor.titanfabric$setPreviousStatusEffect(new StatusEffectInstance(oldEffectInstance));
                        livingEntity.addStatusEffect((StatusEffectInstance) newAccessor);
                    } else {
                        livingEntity.addStatusEffect(newEffectInstance);
                    }
                } else {
                    // No need to set the previous status effect; it cancels out
                    livingEntity.addStatusEffect(newEffectInstance);
                }

                this.changedEffects.add(oppositeEffect);
            }

        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        if (!(entityHitResult.getEntity().getWorld() instanceof ServerWorld serverWorld)) return;
        if (entity instanceof LivingEntity target) {
            for (var entry : getEffectOpposites().getDataMap().entrySet()) {
                RegistryEntry<StatusEffect> effect = entry.getKey();
                RegistryEntry<StatusEffect> oppositeEffect = entry.getValue();
                updateStatusEffects(target, effect);
                updateStatusEffects(target, oppositeEffect);
            }
            if (target.isOnFire()) {
                entity.extinguish();
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0));
            } else if (target.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                target.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
                target.setOnFireFor(10);
            }
            for (int i = 0; i < 32; ++i) {
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        target.getX(),
                        this.getY(),
                        target.getZ(),
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

    private BiDirectionalLookup<RegistryEntry<StatusEffect>> getEffectOpposites() {
        Set<Pair<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>>> opposites = new HashSet<>();
        opposites.add(new Pair<>(StatusEffects.BLINDNESS, StatusEffects.NIGHT_VISION));
        opposites.add(new Pair<>(StatusEffects.POISON, StatusEffects.REGENERATION));
        opposites.add(new Pair<>(StatusEffects.WEAKNESS, StatusEffects.STRENGTH));
        opposites.add(new Pair<>(StatusEffects.SLOWNESS, StatusEffects.SPEED));
        return new BiDirectionalLookup<>(opposites);
    }
}
