package net.shirojr.titanfabric.cca.implementation;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.shirojr.titanfabric.TitanFabricComponents;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;
import net.shirojr.titanfabric.init.TitanFabricDamageTypes;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.init.TitanFabricTags;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Iterator;
import java.util.function.Predicate;

public class FrostburnComponentImpl implements FrostburnComponent, AutoSyncedComponent {
    private final LivingEntity provider;

    private float frostburn;
    private float frostburnLimit;
    private long tick;
    private int frostburnTickSpeed = 40;
    private Phase currentPhase = Phase.INCREASE;

    public FrostburnComponentImpl(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public LivingEntity getProvider() {
        return provider;
    }

    @Override
    public float getMissingHealth() {
        return Math.max(0, getLimitedMaxHealth() - provider.getHealth());
    }

    /**
     * Entity should not die because of Frostburn alone. This is the original max health with a safety gap
     */
    @Override
    public float getMaxAllowedFrostburn() {
        return provider.getMaxHealth() - SAFETY_THRESHOLD;
    }

    @Override
    public int getFrostburnTickSpeed() {
        return frostburnTickSpeed;
    }

    @Override
    public void setFrostburnTickSpeed(int speed) {
        this.frostburnTickSpeed = Math.max(0, speed);
        LoggerUtil.devLogger("set frostburn speed to " + this.frostburnTickSpeed);
    }

    @Override
    public float getLimitedMaxHealth() {
        return provider.getMaxHealth() - getFrostburn();
    }

    @Override
    public float getFrostburn() {
        return frostburn;
    }

    @Override
    public void setFrostburn(float newAmount, boolean limitAmount, boolean shouldSync) {
        this.frostburn = limitAmount ? MathHelper.clamp(newAmount, 0, getMaxAllowedFrostburn()) : newAmount;
        if (shouldSync) {
            sync();
        }
        if (!provider.getWorld().isClient()) {
            LoggerUtil.devLogger("set frostburn value to " + this.frostburn);
        }
    }

    @Override
    public void forceFrostburn(float newFrostburnAmount, boolean shouldSync) {
        if (newFrostburnAmount > getFrostburn()) {
            setPhase(Phase.INCREASE);
        }

        float damageAmount = newFrostburnAmount > getFrostburn() ? CHANGE_AMOUNT : -CHANGE_AMOUNT;
        float maxDamageableHealth = provider.getHealth() - SAFETY_THRESHOLD;
        float trueMissingHealth = provider.getMaxHealth() - provider.getHealth();
        damageAmount = Math.min(damageAmount, maxDamageableHealth);
        if (damageAmount > 0 && trueMissingHealth + damageAmount <= newFrostburnAmount) {
            LoggerUtil.devLogger("Forced %s damage".formatted(damageAmount));
            this.provider.damage(TitanFabricDamageTypes.of(provider.getWorld(), TitanFabricDamageTypes.FROSTBURN), damageAmount);
        }

        this.frostburn = newFrostburnAmount;
        LoggerUtil.devLogger("New forced Frostburn amount: " + this.frostburn);
        if (shouldSync) {
            this.sync();
        }
    }

    @Override
    public float getFrostburnLimit() {
        return frostburnLimit;
    }

    @Override
    public void setFrostburnLimit(float limit, boolean shouldSync) {
        if (limit > this.frostburnLimit) {
            this.setPhase(Phase.INCREASE);
        }
        this.frostburnLimit = MathHelper.clamp(limit, 0, getMaxAllowedFrostburn());
        if (!provider.getWorld().isClient()) {
            LoggerUtil.devLogger("set frostburn target limit to " + this.frostburnLimit);
        }
    }

    @Override
    public Phase getPhase() {
        return this.currentPhase;
    }

    @Override
    public void setPhase(Phase phase) {
        this.currentPhase = phase;
    }

    @Override
    public boolean shouldMaintainFrostburn(int hotBlocksSearchRange, int hotBlocksAmountForThawing, Predicate<BlockState> isHotBlock) {
        if (this.getFrostburnTickSpeed() <= 0) return true;
        boolean advancedThawing = provider.getWorld().getGameRules().getBoolean(TitanFabricGamerules.ADVANCED_FROSTBURN_THAWING);
        if (advancedThawing) {
            if (provider.isFrozen()) return true;
            if (provider.isOnFire()) return false;
        }
        if (provider.hasStatusEffect(TitanFabricStatusEffects.FROSTBURN)) return true;
        if (!advancedThawing) return false;

        RegistryEntry<Biome> currentBiome = provider.getWorld().getBiome(provider.getBlockPos());
        if (currentBiome.isIn(ConventionalBiomeTags.IS_HOT)) {
            return false;
        }
        if (currentBiome.isIn(ConventionalBiomeTags.IS_COLD)) {
            if (hotBlocksSearchRange <= 0) {
                return true;
            }
            int necessaryBlockAmount = Math.max(1, hotBlocksAmountForThawing);
            int blockCount = 0;
            Iterator<BlockPos> searchIterator = BlockPos.iterateOutwards(
                    provider.getBlockPos(),
                    hotBlocksSearchRange, hotBlocksSearchRange, hotBlocksSearchRange
            ).iterator();
            while (searchIterator.hasNext() && blockCount < necessaryBlockAmount) {
                BlockState state = provider.getWorld().getBlockState(searchIterator.next());
                if (isHotBlock.test(state)) {
                    blockCount++;
                }
            }
            return blockCount < necessaryBlockAmount;
        }
        return false;
    }

    @Override
    public boolean shouldMaintainFrostburn() {
        GameRules gameRules = provider.getWorld().getGameRules();
        if (!gameRules.getBoolean(TitanFabricGamerules.ADVANCED_FROSTBURN_THAWING)) {
            return this.shouldMaintainFrostburn(-1, 1, blockState -> true);
        }
        int hotBlocksSearchRange = gameRules.getInt(TitanFabricGamerules.HOT_BLOCK_SEARCH_RANGE);
        int minHotBlockAmount = gameRules.getInt(TitanFabricGamerules.HOT_BLOCK_AMOUNT_FOR_THAWING);
        Predicate<BlockState> isHotBlock = blockState -> {
            boolean hasHotTag = blockState.isIn(TitanFabricTags.Blocks.HOT_BLOCKS);
            if (blockState.contains(Properties.LIT)) {
                if (blockState.get(Properties.LIT)) {
                    return hasHotTag;
                }
                return false;
            }
            return hasHotTag;
        };
        return this.shouldMaintainFrostburn(hotBlocksSearchRange, minHotBlockAmount, isHotBlock);
    }

    @Override
    public void sync() {
        TitanFabricComponents.FROSTBURN.sync(provider);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        if (player.equals(provider)) return true;
        MinecraftServer server = player.getServer();
        if (server == null) return false;
        int viewDistance = server.getPlayerManager().getViewDistance();
        return player.squaredDistanceTo(provider) <= viewDistance * viewDistance;
    }

    @Override
    public void serverTick() {
        if (this.frostburnTickSpeed == 0) {
            return;
        }
        this.tick++;
        if (this.tick % this.frostburnTickSpeed != 0) {
            return;
        }

        if (getPhase().equals(Phase.INCREASE)) {
            if (getFrostburn() != getFrostburnLimit()) {
                float changeAmount = getFrostburn() > getFrostburnLimit() ? -CHANGE_AMOUNT : CHANGE_AMOUNT;
                this.forceFrostburn(getFrostburn() + changeAmount, true);
                if (getFrostburn() == getFrostburnLimit()) {
                    this.setPhase(Phase.DECREASE);
                }
            }
        } else {
            if (!shouldMaintainFrostburn()) {
                this.setFrostburn(this.getFrostburn() - CHANGE_AMOUNT, true, true);
                if (getFrostburn() == 0) {
                    setFrostburnLimit(0, true);
                    setPhase(Phase.INCREASE);
                }
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbtCompound.contains("frostburn")) {
            NbtCompound frostburnNbt = nbtCompound.getCompound("frostburn");
            this.setFrostburn(frostburnNbt.getFloat("currentFrostburn"), false, false);
            this.setFrostburnLimit(frostburnNbt.getFloat("limit"), true);
            this.setFrostburnTickSpeed(frostburnNbt.getInt("tickSpeed"));
            this.setPhase(Phase.values()[frostburnNbt.getInt("phase")]);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound frostburnNbt = new NbtCompound();
        frostburnNbt.putFloat("currentFrostburn", this.getFrostburn());
        frostburnNbt.putFloat("limit", this.getFrostburnLimit());
        frostburnNbt.putInt("tickSpeed", this.getFrostburnTickSpeed());
        frostburnNbt.putInt("phase", this.getPhase().ordinal());

        nbtCompound.put("frostburn", frostburnNbt);
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeFloat(this.getFrostburn());
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.setFrostburn(buf.readFloat(), false, false);
    }
}
