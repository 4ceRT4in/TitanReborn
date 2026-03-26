package net.shirojr.titanfabric.cca.implementation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.TitanFabricComponents;
import net.shirojr.titanfabric.cca.component.DiamondAbsorptionComponent;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.util.effects.DiamondAbsorptionHelper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class DiamondAbsorptionComponentImpl implements DiamondAbsorptionComponent, AutoSyncedComponent {
    private static final float EPSILON = 0.01f;

    private final LivingEntity provider;

    private float diamondAbsorptionAmount;
    private float retainedAbsorptionCap;
    private float lastObservedAbsorptionAmount;
    private int lastObservedEffectAmplifier = -1;
    private int lastObservedEffectDuration;

    public DiamondAbsorptionComponentImpl(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public float getDiamondAbsorptionAmount() {
        return diamondAbsorptionAmount;
    }

    @Override
    public void setDiamondAbsorptionAmount(float amount, boolean shouldSync) {
        float sanitized = Math.max(0.0f, amount);
        if (Math.abs(this.diamondAbsorptionAmount - sanitized) < EPSILON) return;

        this.diamondAbsorptionAmount = sanitized;
        if (shouldSync) {
            sync();
        }
    }

    @Override
    public void sync() {
        TitanFabricComponents.DIAMOND_ABSORPTION.sync(provider);
    }

    @Override
    public void serverTick() {
        StatusEffectInstance effectInstance = provider.getStatusEffect(TitanFabricStatusEffects.DIAMOND_ABSORPTION);
        float currentAbsorption = provider.getAbsorptionAmount();

        if (effectInstance != null) {
            int amplifier = effectInstance.getAmplifier();
            int duration = effectInstance.getDuration();
            float targetAmount = DiamondAbsorptionHelper.getAbsorptionAmount(amplifier);
            DiamondAbsorptionHelper.applyMaxAbsorptionModifier(provider, amplifier);
            this.retainedAbsorptionCap = Math.max(this.retainedAbsorptionCap, Math.max(currentAbsorption, targetAmount));

            if (amplifier != this.lastObservedEffectAmplifier || duration > this.lastObservedEffectDuration) {
                float updatedAbsorption = Math.max(currentAbsorption, targetAmount);
                if (updatedAbsorption > currentAbsorption) {
                    provider.setAbsorptionAmount(updatedAbsorption);
                    currentAbsorption = provider.getAbsorptionAmount();
                }

                setDiamondAbsorptionAmount(Math.min(currentAbsorption, targetAmount), true);
                this.lastObservedAbsorptionAmount = currentAbsorption;
            }

            this.lastObservedEffectAmplifier = amplifier;
            this.lastObservedEffectDuration = duration;
        } else {
            this.lastObservedEffectAmplifier = -1;
            this.lastObservedEffectDuration = 0;
            if (this.diamondAbsorptionAmount > 0.0f) {
                setDiamondAbsorptionAmount(0.0f, true);
            }

            if (this.retainedAbsorptionCap > EPSILON && currentAbsorption > EPSILON) {
                this.retainedAbsorptionCap = currentAbsorption;
                DiamondAbsorptionHelper.applyMaxAbsorptionModifier(provider.getAttributes(), currentAbsorption);
            } else {
                this.retainedAbsorptionCap = 0.0f;
                DiamondAbsorptionHelper.clearMaxAbsorptionModifier(provider);
            }
        }

        if (this.diamondAbsorptionAmount > currentAbsorption + EPSILON) {
            setDiamondAbsorptionAmount(currentAbsorption, true);
        }

        this.lastObservedAbsorptionAmount = currentAbsorption;
    }

    @Override
    public void readFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (!nbt.contains("diamond_absorption")) return;

        NbtCompound data = nbt.getCompound("diamond_absorption");
        this.diamondAbsorptionAmount = data.getFloat("amount");
        this.retainedAbsorptionCap = data.getFloat("retained_cap");
        this.lastObservedAbsorptionAmount = data.getFloat("last_absorption");
        this.lastObservedEffectAmplifier = data.getInt("last_amplifier");
        this.lastObservedEffectDuration = data.getInt("last_duration");
    }

    @Override
    public void writeToNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound data = new NbtCompound();
        data.putFloat("amount", this.diamondAbsorptionAmount);
        data.putFloat("retained_cap", this.retainedAbsorptionCap);
        data.putFloat("last_absorption", this.lastObservedAbsorptionAmount);
        data.putInt("last_amplifier", this.lastObservedEffectAmplifier);
        data.putInt("last_duration", this.lastObservedEffectDuration);
        nbt.put("diamond_absorption", data);
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeFloat(this.diamondAbsorptionAmount);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.diamondAbsorptionAmount = buf.readFloat();
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player.equals(provider);
    }
}
