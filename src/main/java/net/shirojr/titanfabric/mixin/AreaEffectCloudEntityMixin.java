package net.shirojr.titanfabric.mixin;

import com.google.common.collect.Lists;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.particles.GasParticleEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudEntityMixin {

    @Shadow
    public abstract float getRadius();

    @Shadow
    public abstract void setRadius(float radius);

    @Shadow
    public abstract void calculateDimensions();

    @Shadow
    public abstract int getColor();

    @Final
    @Shadow
    private Map<Entity, Integer> affectedEntities;

    @Shadow
    private Potion potion;
    @Final
    @Shadow
    private List<StatusEffectInstance> effects;

    @Shadow
    private int reapplicationDelay;

    @Shadow
    private float radiusOnUse;

    // Custom fields for separate axis dimensions
    private float customRadiusX = 1.0F; // Updated starting size
    private float customRadiusY = 1.0F; // Updated starting size
    private float customRadiusZ = 1.0F; // Updated starting size

    // Define maximum and minimum dimensions
    private static final float MAX_RADIUS_X = 8.0F;
    private static final float MAX_RADIUS_Y = 6.0F;
    private static final float MAX_RADIUS_Z = 8.0F;

    private static final float MIN_RADIUS_X = 1.0F; // Updated minimum size
    private static final float MIN_RADIUS_Y = 1.0F; // Updated minimum size
    private static final float MIN_RADIUS_Z = 1.0F; // Updated minimum size

    // Define growth/shrink rates per tick
    private static final float RADIUS_GROWTH_X = 0.1F;
    private static final float RADIUS_GROWTH_Y = 0.075F;
    private static final float RADIUS_GROWTH_Z = 0.1F;

    // Random instance for particle positioning
    private final Random random = new Random();


    /**
     * Modify the argument passed to setRadius(float radius) in the constructor.
     *
     * @param originalRadius The original radius value (expected to be 3.0F).
     * @return The new radius value (1.0F).
     */
    @ModifyArg(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setRadius(F)V"
            ),
            index = 0 // The index of the argument to modify
    )
    private float modifySetRadiusArgument(float originalRadius) {
        return 1.0F; // Set the radius to 1.0F instead of 3.0F
    }

    /**
     * Redirects vanilla setRadius calls within the tick method to prevent interference.
     *
     * @param instance The AreaEffectCloudEntity instance.
     * @param radius   The radius value attempted to set by vanilla.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setRadius(F)V"
            )
    )
    private void preventVanillaSetRadius(AreaEffectCloudEntity instance, float radius) {
        // Do nothing to prevent vanilla setRadius from modifying custom radii
    }

    /**
     * Injects code at the start of the tick method to manage custom bounding box dimensions.
     *
     * @param ci CallbackInfo for the injection point.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void manageCustomBoundingBox(CallbackInfo ci) {

        AreaEffectCloudEntity thisEntity = ((AreaEffectCloudEntity) (Object) this);


        int ticksAlive = thisEntity.age;
        int duration = thisEntity.getDuration();

        // Prevent division by zero
        if (duration <= 0) return;

        float progress = (float) ticksAlive / (float) duration;

        if (progress < 0.75F) {
            // First half: Expand radii
            expandRadius('X');
            expandRadius('Y');
            expandRadius('Z');
        } else {
            // Second half: Shrink radii
            shrinkRadius('X');
            shrinkRadius('Y');
            shrinkRadius('Z');
        }
        updateBoundingBox();

        if (!thisEntity.world.isClient) {
            // Execute logic every 5 ticks
            if (thisEntity.age % 5 == 0) {
                // Remove expired affected entities
                Iterator<Map.Entry<Entity, Integer>> iterator = this.affectedEntities.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Entity, Integer> entry = iterator.next();
                    if (thisEntity.age >= entry.getValue()) {
                        iterator.remove();
                    }
                }

                // Prepare list of status effects to apply
                List<StatusEffectInstance> effectsToApply = Lists.newArrayList();

                for (StatusEffectInstance effectInstance : this.potion.getEffects()) {
                    effectsToApply.add(
                            new StatusEffectInstance(
                                    effectInstance.getEffectType(),
                                    effectInstance.getDuration() / 4, // Adjust duration as needed
                                    effectInstance.getAmplifier(),
                                    effectInstance.isAmbient(),
                                    effectInstance.shouldShowParticles()
                            )
                    );
                }

                effectsToApply.addAll(this.effects);

                // If no effects to apply, clear affected entities
                if (effectsToApply.isEmpty()) {
                    this.affectedEntities.clear();
                } else {
                    // Define the standard bounding box (Axis-Aligned Bounding Box)
                    Box standardBoundingBox = thisEntity.getBoundingBox();

                    // Retrieve entities within the standard bounding box
                    List<LivingEntity> nearbyEntities = thisEntity.world.getNonSpectatingEntities(LivingEntity.class, standardBoundingBox);

                    if (!nearbyEntities.isEmpty()) {
                        for (LivingEntity livingEntity : nearbyEntities) {
                            // Skip if already affected
                            if (this.affectedEntities.containsKey(livingEntity)) {
                                continue;
                            }

                            // Check if the living entity is affected by splash potions
                            if (!livingEntity.isAffectedBySplashPotions()) {
                                continue;
                            }

                            // Calculate the relative position of the entity to the center of the cloud
                            double relativeX = livingEntity.getX() - thisEntity.getX();
                            double relativeZ = livingEntity.getZ() - thisEntity.getZ();

                            // Perform the half-ellipsoid check
                            if (isWithinEllipse(relativeX, relativeZ, customRadiusX, customRadiusZ)) {
                                // Mark the entity as affected with a reapplication delay
                                this.affectedEntities.put(livingEntity, thisEntity.age + this.reapplicationDelay);

                                // Apply all status effects to the entity
                                for (StatusEffectInstance effectInstance : effectsToApply) {
                                    if (effectInstance.getEffectType().isInstant()) {
                                        effectInstance.getEffectType().applyInstantEffect(
                                                thisEntity,
                                                thisEntity.getOwner(),
                                                livingEntity,
                                                effectInstance.getAmplifier(),
                                                0.5 // Factor as needed
                                        );
                                    } else {
                                        livingEntity.addStatusEffect(new StatusEffectInstance(effectInstance), thisEntity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private boolean isWithinEllipse(double relativeX, double relativeZ, double radiusX, double radiusZ) {
        // Normalize coordinates
        double normX = relativeX / radiusX;
        double normZ = relativeZ / radiusZ;

        // Ellipse equation: (x/a)^2 + (z/c)^2 <= 1
        return (normX * normX) + (normZ * normZ) <= 1.0;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addImportantParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
            )
    )
    private void preventVanillaParticles(World world, ParticleEffect particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        // Do nothing to prevent vanilla particles from spawning
    }

    /**
     * Injects code at the end of the tick method to spawn custom gas particles.
     *
     * @param ci CallbackInfo for the injection point.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void injectGasParticles(CallbackInfo ci) {
        AreaEffectCloudEntity self = (AreaEffectCloudEntity) (Object) this;

        if (self.world.isClient) {
            // Define the radii for the ellipsoid along each axis
            double radiusX = customRadiusX;
            double radiusY = customRadiusY;
            double radiusZ = customRadiusZ;

            // Extract RGB components from the entity's color
            int color = self.getColor();
            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;

            // Particle properties
            float baseScale = 0.5F; // Base scale for smoke particles
            float alpha = 1F;      // Semi-transparent for smoke effect
            double densityFactor = 2.0; // Adjust to control particle density

            // Noise factor to perturb particle positions
            double noiseMagnitude = 0.2; // Increased to provide more noticeable noise

            // Calculate the volume of a half-ellipsoid
            double volume = (4.0 / 3.0) * Math.PI * radiusX * radiusY * radiusZ / 2.0;

            // Determine the number of particles based on volume and density factor
            int particles = (int) (volume * densityFactor);

            // Spawn particles within the half-ellipsoid with increased edge noise
            for (int i = 0; i < particles; i++) {
                // Uniformly sample within the volume using inverse transform sampling
                double u = random.nextDouble();
                double v = random.nextDouble();
                double w = random.nextDouble();

                double theta = u * 2.0 * Math.PI;       // Angle around the Y-axis [0, 2π]
                double phi = Math.acos(1 - v);         // Angle from the Y-axis [0, π/2] for half-ellipsoid
                double r = Math.cbrt(w);               // Cube root ensures uniform distribution

                // Convert spherical coordinates to Cartesian coordinates and scale by radii
                double xOffset = r * Math.sin(phi) * Math.cos(theta) * radiusX;
                double yOffset = r * Math.cos(phi) * radiusY;            // Ensure y is positive for half-ellipsoid
                double zOffset = r * Math.sin(phi) * Math.sin(theta) * radiusZ;

                // Calculate the normalized distance from the center
                double normalizedDistance = Math.sqrt(
                        Math.pow(xOffset / radiusX, 2) +
                                Math.pow(yOffset / radiusY, 2) +
                                Math.pow(zOffset / radiusZ, 2)
                );

                // **Corrected Noise Factor: Apply more noise near the edges**
                double edgeNoiseFactor = normalizedDistance * noiseMagnitude;

                // Randomly perturb the position with increased noise near the edges
                double x = self.getX() + xOffset + (random.nextDouble() - 0.5) * edgeNoiseFactor * radiusX;
                double y = (self.getY() + yOffset + (random.nextDouble() - 0.5) * edgeNoiseFactor * radiusY) - 1;
                double z = self.getZ() + zOffset + (random.nextDouble() - 0.5) * edgeNoiseFactor * radiusZ;

                // Slight upward bias in velocity to simulate rising smoke
                double velocityX = (random.nextDouble() - 0.5) * 0.01;
                double velocityY = 0.01 + (random.nextDouble() * 0.01); // Upward movement
                double velocityZ = (random.nextDouble() - 0.5) * 0.01;

                // Randomize scale slightly for variation
                float scale = baseScale + random.nextFloat() * 0.5F; // Scale between 0.5 and 1.0

                // Randomize alpha for depth
                float particleAlpha = alpha - (random.nextFloat() * 0.1F); // Alpha between 0.4 and 0.5

                // Spawn the particle
                self.world.addParticle(
                        new GasParticleEffect(
                                red,
                                green,
                                blue,
                                scale,
                                particleAlpha
                        ),
                        x, y, z,
                        velocityX, velocityY, velocityZ
                );
            }
        }
    }





    /**
     * Expands the radius along the specified axis if possible.
     *
     * @param axis The axis to expand ('X', 'Y', 'Z').
     */
    private void expandRadius(char axis) {
        switch (axis) {
            case 'X':
                if (customRadiusX < MAX_RADIUS_X && canExpandAlongAxis(axis)) {
                    customRadiusX = Math.min(customRadiusX + RADIUS_GROWTH_X, MAX_RADIUS_X);
                }
                break;
            case 'Y':
                if (customRadiusY < MAX_RADIUS_Y && canExpandAlongAxis(axis)) {
                    customRadiusY = Math.min(customRadiusY + RADIUS_GROWTH_Y, MAX_RADIUS_Y);
                }
                break;
            case 'Z':
                if (customRadiusZ < MAX_RADIUS_Z && canExpandAlongAxis(axis)) {
                    customRadiusZ = Math.min(customRadiusZ + RADIUS_GROWTH_Z, MAX_RADIUS_Z);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Shrinks the radius along the specified axis if possible.
     *
     * @param axis The axis to shrink ('X', 'Y', 'Z').
     */
    private void shrinkRadius(char axis) {
        switch (axis) {
            case 'X':
                if (customRadiusX > MIN_RADIUS_X) {
                    customRadiusX = Math.max(customRadiusX - RADIUS_GROWTH_X, MIN_RADIUS_X);
                }
                break;
            case 'Y':
                if (customRadiusY > MIN_RADIUS_Y) {
                    customRadiusY = Math.max(customRadiusY - RADIUS_GROWTH_Y, MIN_RADIUS_Y);
                }
                break;
            case 'Z':
                if (customRadiusZ > MIN_RADIUS_Z) {
                    customRadiusZ = Math.max(customRadiusZ - RADIUS_GROWTH_Z, MIN_RADIUS_Z);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Checks if the bounding box can expand along the specified axis without colliding with solid blocks.
     *
     * @param axis The axis along which to check ('X', 'Y', 'Z').
     * @return True if expansion is possible; false otherwise.
     */
    private boolean canExpandAlongAxis(char axis) {
        /*AreaEffectCloudEntity thisEntity = (AreaEffectCloudEntity) (Object) this;
        World world = thisEntity.world;

        // Define the direction vector based on the axis
        double dx = 0.0, dy = 0.0, dz = 0.0;
        switch (axis) {
            case 'X':
                dx = 1.0;
                break;
            case 'Y':
                dy = 1.0; // Only allow upward expansion
                break;
            case 'Z':
                dz = 1.0;
                break;
            default:
                return false;
        }

        // Calculate the potential new bounding box dimensions
        float newRadiusX = customRadiusX + (axis == 'X' ? RADIUS_GROWTH_X : 0.0F);
        float newRadiusY = customRadiusY + (axis == 'Y' ? RADIUS_GROWTH_Y : 0.0F);
        float newRadiusZ = customRadiusZ + (axis == 'Z' ? RADIUS_GROWTH_Z : 0.0F);

        // Create a new bounding box with the potential expansion
        Box potentialBox;
        if (axis == 'Y') {
            // Only expand upwards for Y axis to prevent overlapping with the ground
            potentialBox = new Box(
                    thisEntity.getX() - customRadiusX,
                    thisEntity.getY(), // No downward expansion
                    thisEntity.getZ() - customRadiusZ,
                    thisEntity.getX() + customRadiusX,
                    thisEntity.getY() + newRadiusY,
                    thisEntity.getZ() + customRadiusZ
            );
        } else {
            // Expand normally for X and Z axes, but keep Y consistent
            potentialBox = new Box(
                    thisEntity.getX() - newRadiusX,
                    thisEntity.getY(),
                    thisEntity.getZ() - newRadiusZ,
                    thisEntity.getX() + newRadiusX,
                    thisEntity.getY() + customRadiusY, // Y remains the same to prevent downward expansion
                    thisEntity.getZ() + newRadiusZ
            );
        }

        // Iterate through all block positions within the potentialBox
        int minX = MathHelper.floor(potentialBox.minX);
        int minY = MathHelper.floor(potentialBox.minY);
        int minZ = MathHelper.floor(potentialBox.minZ);
        int maxX = MathHelper.ceil(potentialBox.maxX);
        int maxY = MathHelper.ceil(potentialBox.maxY);
        int maxZ = MathHelper.ceil(potentialBox.maxZ);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).isSolidBlock(world, pos)) {
                        return false; // Collision detected; cannot expand
                    }
                }
            }
        }*/

        // No solid blocks found within the potentialBox
        return true;
    }




    /**
     * Updates the bounding box based on custom dimensions.
     */
    private void updateBoundingBox() {
        AreaEffectCloudEntity thisEntity = (AreaEffectCloudEntity) (Object) this;

        double x = thisEntity.getX();
        double y = thisEntity.getY();
        double z = thisEntity.getZ();

        // Create a new bounding box based on custom dimensions
        Box newBoundingBox = new Box(
                x - customRadiusX, y, z - customRadiusZ,
                x + customRadiusX, y + customRadiusY, z + customRadiusZ
        );

        // Set the new bounding box
        ((AreaEffectCloudEntity) (Object) this).setBoundingBox(newBoundingBox);

    }

    /**
     * Overrides the getDimensions method to reflect custom bounding box dimensions.
     *
     * @param pose The pose of the entity.
     * @return The custom EntityDimensions.
     */
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getCustomDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        float width = Math.max(customRadiusX, customRadiusZ) * 2.0F;
        float height = customRadiusY * 2.0F;

        cir.setReturnValue(EntityDimensions.changing(width, height));
    }

    /**
     * Injects into the setRadius method to adjust custom radii and update the bounding box.
     *
     * @param radius The new radius.
     * @param ci     CallbackInfo for the injection point.
     */
    @Inject(method = "setRadius", at = @At("HEAD"))
    private void onSetRadius(float radius, CallbackInfo ci) {
        AreaEffectCloudEntity cloudEntity = (AreaEffectCloudEntity) (Object) this;
        // Update customRadiusX, customRadiusY, customRadiusZ based on the new radius
        customRadiusX = radius;
        customRadiusY = radius;
        customRadiusZ = radius;

        // Recalculate and update the bounding box
        cloudEntity.calculateDimensions();
        updateBoundingBox();
    }
}