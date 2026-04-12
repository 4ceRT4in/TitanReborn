package net.shirojr.titanfabric.cca.implementation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.shirojr.titanfabric.TitanFabricComponents;
import net.shirojr.titanfabric.cca.component.RecoveryBufferComponent;
import net.shirojr.titanfabric.util.effects.RecoveryProfile;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;

public class RecoveryBufferComponentImpl implements RecoveryBufferComponent, AutoSyncedComponent {
    private static final float EPSILON = 0.01f;

    private final LivingEntity provider;

    @Nullable
    private RecoveryProfile activeProfile;
    @Nullable
    private RecoveryProfile lastObservedProfile;

    private float bufferAmount;
    private boolean healingActive;
    private int refillTicks;
    private int healTicks;
    private int lastObservedEffectDuration;

    public RecoveryBufferComponentImpl(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public float getBufferAmount() {
        return bufferAmount;
    }

    @Override
    public float getMaxBufferAmount() {
        return activeProfile != null ? activeProfile.getMaxBufferAmount() : 0.0f;
    }

    @Override
    public @Nullable RecoveryProfile getActiveProfile() {
        return activeProfile;
    }

    @Override
    public boolean isHealingActive() {
        return healingActive;
    }

    @Override
    public void sync() {
        TitanFabricComponents.RECOVERY_BUFFER.sync(provider);
    }

    @Override
    public void serverTick() {
        RecoveryProfile.ActiveRecovery activeRecovery = RecoveryProfile.getActive(provider);
        if (activeRecovery == null) {
            clearState(true);
            return;
        }

        RecoveryProfile profile = activeRecovery.profile();
        int duration = activeRecovery.duration();
        pruneSecondaryRecoveryEffects(activeRecovery);

        if (profile != this.lastObservedProfile || this.activeProfile == null) {
            this.activeProfile = profile;
            this.bufferAmount = profile.getMaxBufferAmount();
            this.healingActive = false;
            this.refillTicks = 0;
            this.healTicks = 0;
            sync();
        } else if (this.activeProfile == null) {
            this.activeProfile = profile;
        }

        this.lastObservedProfile = profile;
        this.lastObservedEffectDuration = duration;

        tickHealing(profile);
        tickRefill(profile);
    }

    private void pruneSecondaryRecoveryEffects(RecoveryProfile.ActiveRecovery activeRecovery) {
        for (StatusEffectInstance effectInstance : new ArrayList<>(provider.getStatusEffects())) {
            if (!(effectInstance.getEffectType().value() instanceof net.shirojr.titanfabric.effect.RecoveryStatusEffect)) continue;
            if (effectInstance.getEffectType().equals(activeRecovery.effectType())) continue;
            provider.removeStatusEffect(effectInstance.getEffectType());
        }
    }

    private void tickRefill(RecoveryProfile profile) {
        this.refillTicks++;
        if (this.refillTicks < profile.getRefillIntervalTicks()) return;
        this.refillTicks = 0;

        float newBufferAmount = Math.min(profile.getMaxBufferAmount(), this.bufferAmount + profile.getRefillAmount());
        if (Math.abs(newBufferAmount - this.bufferAmount) < EPSILON) return;

        this.bufferAmount = newBufferAmount;
        sync();
    }

    private void tickHealing(RecoveryProfile profile) {
        if (!this.healingActive && canTriggerHealing(profile)) {
            this.healingActive = true;
            this.healTicks = 0;
            sync();
        }

        if (!this.healingActive) return;
        if (shouldStopHealing(profile)) {
            stopHealing(true);
            return;
        }

        this.healTicks++;
        if (this.healTicks < profile.getHealIntervalTicks()) return;
        this.healTicks = 0;

        float healAmount = Math.min(profile.getHealAmount(), this.bufferAmount);
        healAmount = Math.min(healAmount, provider.getMaxHealth() - provider.getHealth());

        if (healAmount <= EPSILON) {
            stopHealing(true);
            return;
        }

        provider.heal(healAmount);
        this.bufferAmount = Math.max(0.0f, this.bufferAmount - healAmount);
        spawnHealParticles();
        if (shouldStopHealing(profile)) {
            this.healingActive = false;
            this.healTicks = 0;
        }
        sync();
    }

    private boolean canTriggerHealing(RecoveryProfile profile) {
        return this.bufferAmount > EPSILON
                && provider.getHealth() <= profile.getTriggerThresholdHealth()
                && provider.getHealth() < provider.getMaxHealth();
    }

    private boolean shouldStopHealing(RecoveryProfile profile) {
        return this.bufferAmount <= EPSILON
                || provider.getHealth() >= provider.getMaxHealth()
                || this.activeProfile == null;
    }

    private void stopHealing(boolean shouldSync) {
        boolean wasHealing = this.healingActive;
        this.healingActive = false;
        this.healTicks = 0;
        if (shouldSync && wasHealing) {
            sync();
        }
    }

    private void clearState(boolean shouldSync) {
        boolean hadState = this.activeProfile != null || this.bufferAmount > EPSILON || this.healingActive;

        this.activeProfile = null;
        this.lastObservedProfile = null;
        this.bufferAmount = 0.0f;
        this.healingActive = false;
        this.refillTicks = 0;
        this.healTicks = 0;
        this.lastObservedEffectDuration = 0;

        if (hadState && shouldSync) {
            sync();
        }
    }

    private void spawnHealParticles() {
        if (!(provider.getWorld() instanceof ServerWorld serverWorld) || this.activeProfile == null) return;

        double x = provider.getX();
        double y = provider.getBodyY(0.6);
        double z = provider.getZ();
        for (int color : getParticleGradient(this.activeProfile)) {
            float red = ((color >> 16) & 0xFF) / 255.0f;
            float green = ((color >> 8) & 0xFF) / 255.0f;
            float blue = (color & 0xFF) / 255.0f;

            serverWorld.spawnParticles(
                    EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, red, green, blue),
                    x,
                    y,
                    z,
                    3,
                    0.28,
                    0.32,
                    0.28,
                    0.04
            );
        }
    }

    private static int[] getParticleGradient(RecoveryProfile profile) {
        return switch (profile) {
            case BASE, LONG -> new int[]{0xF9CB40, 0xF27E3E, 0xFC4442, 0xD92B35, 0x6C161A};
            case STRONG -> new int[]{0xFF8181, 0xFF0606, 0xC6223B, 0xA10E0A};
        };
    }

    @Override
    public void readFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (!nbt.contains("recovery_buffer")) return;

        NbtCompound data = nbt.getCompound("recovery_buffer");
        this.activeProfile = data.contains("profile") ? RecoveryProfile.byId(data.getString("profile")) : null;
        this.lastObservedProfile = data.contains("last_profile") ? RecoveryProfile.byId(data.getString("last_profile")) : null;
        this.bufferAmount = data.getFloat("buffer");
        this.healingActive = data.getBoolean("healing_active");
        this.refillTicks = data.getInt("refill_ticks");
        this.healTicks = data.getInt("heal_ticks");
        this.lastObservedEffectDuration = data.getInt("last_duration");
    }

    @Override
    public void writeToNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound data = new NbtCompound();
        if (this.activeProfile != null) {
            data.putString("profile", this.activeProfile.getId());
        }
        if (this.lastObservedProfile != null) {
            data.putString("last_profile", this.lastObservedProfile.getId());
        }
        data.putFloat("buffer", this.bufferAmount);
        data.putBoolean("healing_active", this.healingActive);
        data.putInt("refill_ticks", this.refillTicks);
        data.putInt("heal_ticks", this.healTicks);
        data.putInt("last_duration", this.lastObservedEffectDuration);
        nbt.put("recovery_buffer", data);
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.activeProfile == null ? -1 : this.activeProfile.ordinal());
        buf.writeFloat(this.bufferAmount);
        buf.writeBoolean(this.healingActive);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        int profileOrdinal = buf.readInt();
        this.activeProfile = profileOrdinal < 0 ? null : RecoveryProfile.values()[profileOrdinal];
        this.bufferAmount = buf.readFloat();
        this.healingActive = buf.readBoolean();
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player.equals(provider);
    }
}
