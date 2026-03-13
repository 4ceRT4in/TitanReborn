package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.network.packet.ImmunityBlockedEffectPacket;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ImmunityEffect extends StatusEffect {
    private static final Map<UUID, StatusEffect> entityBlockedEffect = new ConcurrentHashMap<>();

    public ImmunityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Nullable
    public static StatusEffect getBlockedEffects(UUID uuid) {
        return entityBlockedEffect.get(uuid);
    }

    public static void setBlockedEffect(UUID uuid, @Nullable StatusEffect effect) {
        if (effect == null) {
            entityBlockedEffect.remove(uuid);
            return;
        }
        entityBlockedEffect.put(uuid, effect);
    }

    public static void clearBlockedEffect(UUID uuid) {
        entityBlockedEffect.remove(uuid);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient()) return false;

        StatusEffectInstance immunityEffect = entity.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);
        if (immunityEffect != null && immunityEffect.getDuration() <= 1) {
            UUID uuid = entity.getUuid();
            resetImmunity(entity);
            LoggerUtil.devLogger("Immunity expired for " + uuid + ". Cleared immunity effect");
        }

        return false;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }


    public static void checkAndBlockNegativeEffect(LivingEntity entity, StatusEffectInstance newEffect) {
        if (entity.getWorld() == null || entity.getWorld().isClient) return;
        StatusEffectInstance immunityEffect = entity.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);

        if (immunityEffect != null && newEffect.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) {
            UUID uuid = entity.getUuid();
            StatusEffect blocked = entityBlockedEffect.get(uuid);

            if (blocked != null) {
                if (blocked == newEffect.getEffectType().value()) {
                    entity.removeStatusEffect(newEffect.getEffectType());
                    LoggerUtil.devLogger("Immunity for " + uuid + " blocked known effect: " + newEffect.getEffectType().value().getTranslationKey());
                }
            } else {
                StatusEffect newlyBlocked = newEffect.getEffectType().value();
                setBlockedEffect(uuid, newlyBlocked);
                if (entity instanceof ServerPlayerEntity player) {
                    new ImmunityBlockedEffectPacket(uuid, newlyBlocked).sendPacket(player);
                }
                entity.removeStatusEffect(newEffect.getEffectType());
                LoggerUtil.devLogger("Immunity for " + uuid + " now protects against new effect: " + newEffect.getEffectType().value().getTranslationKey());
            }
        }
    }

    public static void resetImmunity(LivingEntity entity) {
        UUID uuid = entity.getUuid();
        clearBlockedEffect(uuid);
        if (entity instanceof ServerPlayerEntity player) {
            new ImmunityBlockedEffectPacket(uuid, Optional.empty()).sendPacket(player);
        }
        LoggerUtil.devLogger("Immunity reset for " + uuid + ". Cleared immunity effect");
    }

    public static void syncBlockedEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        new ImmunityBlockedEffectPacket(uuid, getBlockedEffects(uuid)).sendPacket(player);
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        super.onRemoved(attributeContainer);
    }
}
