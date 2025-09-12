package net.shirojr.titanfabric.cca.component;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.TitanFabricComponents;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricTags;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.function.Predicate;

/**
 * Frostburn is a status where a living entity gets its max health limited.
 * <h3>General Parameters</h3>
 * <ul>
 *     <li><code>frostburnLimit</code> - targeted value of frostburn which will be kept after the build-up finished</li>
 *     <li><code>frostburnTickSpeed</code> - tick steps in which it will increase / decrease frostburn by {@link #CHANGE_AMOUNT}</li>
 * </ul>
 * <p>
 * To start frostburn, use {@link #setFrostburnLimit(float, boolean)} and make sure that the
 * {@link net.shirojr.titanfabric.init.TitanFabricStatusEffects#FROSTBURN TitanFabricStatusEffects#FROSTBURN} is applied
 */
public interface FrostburnComponent extends Component, ServerTickingComponent {
    Identifier IDENTIFIER = TitanFabric.getId("frostburn");
    float CHANGE_AMOUNT = 1.0f;
    float SAFETY_THRESHOLD = 2.0f;

    static FrostburnComponent get(LivingEntity entity) {
        return TitanFabricComponents.FROSTBURN.get(entity);
    }

    @SuppressWarnings("unused")
    LivingEntity getProvider();

    /**
     * @return missing health, excluding frostburned health
     */
    float getMissingHealth();

    /**
     * @return max health of {@link #getProvider() provider} excluding frostburned health
     */
    float getLimitedMaxHealth();

    int getFrostburnTickSpeed();

    void setFrostburnTickSpeed(int speed);

    float getFrostburn();

    /**
     * Sets new frostburn amount with respect to {@link #getProvider()}'s health.<br>
     * Consider using {@link #forceFrostburn(float, boolean)} if {@link #getProvider()} is allowed to lose health.
     *
     * @param newAmount  new frostburn value which is capped at a maximum of {@link #getProvider()}'s actual missing health
     * @param shouldSync sync to client side
     */
    void setFrostburn(float newAmount, boolean limitAmount, boolean shouldSync);

    /**
     * Forces new frostburn value by damaging the {@link #getProvider() provider} if necessary
     */
    void forceFrostburn(float newAmount, boolean shouldSync);

    /**
     * @return The target value which will eventually be reached by increasing {@link #getFrostburn() frostburn} using ticks
     */
    float getFrostburnLimit();

    void setFrostburnLimit(float limit, boolean shouldSync);

    Phase getPhase();

    void setPhase(Phase phase);

    /**
     * @param hotBlocksSearchRange use <code>range <= 0</code> to disable search
     * @return Specifies if {@link #getProvider() provider} should maintain the current frostburn value
     */
    boolean shouldMaintainFrostburn(int hotBlocksSearchRange, int hotBlocksAmountForThawing, Predicate<BlockState> isHotBlock);

    /**
     * <ul>
     *     <li>Hot Block Search range is handled by {@link TitanFabricGamerules#HOT_BLOCK_SEARCH_RANGE}</li>
     *     <li>Hot Block amount is handled by {@link TitanFabricGamerules#HOT_BLOCK_AMOUNT_FOR_THAWING}</li>
     *     <li>Hot Blocks are defined by {@link TitanFabricTags.Blocks#HOT_BLOCKS}</li>
     * </ul>
     *
     * @see #shouldMaintainFrostburn(int, int, Predicate)
     */
    boolean shouldMaintainFrostburn();

    float getMaxAllowedFrostburn();

    void sync();

    enum Phase {
        INCREASE, DECREASE
    }
}
