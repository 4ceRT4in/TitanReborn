package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public interface TitanFabricGamerules {
    GameRules.Key<GameRules.BooleanRule> LEGACY_COMBAT = registerBooleanGamerule("legacyCombat", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> LEGACY_FOOD_REGENERATION = registerBooleanGamerule("legacyFoodRegeneration", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> DISABLE_ELYTRA_BOOSTING = registerBooleanGamerule("disableElytraBoosting", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> ADVANCED_FROSTBURN_THAWING = registerBooleanGamerule("advancedFrostburnThawing", GameRules.Category.MISC, true);
    GameRules.Key<GameRules.IntRule> HOT_BLOCK_SEARCH_RANGE = registerIntegerGamerule("hotBlockSearchRange", GameRules.Category.MISC, 7, -1, null);
    GameRules.Key<GameRules.IntRule> HOT_BLOCK_AMOUNT_FOR_THAWING = registerIntegerGamerule("hotBlockAmountForThawing", GameRules.Category.MISC, 1, 1, null);
    GameRules.Key<GameRules.BooleanRule> GREATSWORD_COOLDOWN = registerBooleanGamerule("greatSwordCooldown", GameRules.Category.PLAYER, true);

    @SuppressWarnings("SameParameterValue")
    private static GameRules.Key<GameRules.BooleanRule> registerBooleanGamerule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    @SuppressWarnings("SameParameterValue")
    private static GameRules.Key<GameRules.IntRule> registerIntegerGamerule(
            String name, GameRules.Category category, int defaultValue, @Nullable Integer min, @Nullable Integer max) {
        if (min != null) {
            if (max != null) {
                return GameRuleRegistry.register(name, category, GameRuleFactory.createIntRule(defaultValue, min, max));
            }
            return GameRuleRegistry.register(name, category, GameRuleFactory.createIntRule(defaultValue, min));
        }
        if (max != null) {
            throw new IllegalArgumentException("Tried to register Integer Gamerule with max value and without min value");
        }
        return GameRuleRegistry.register(name, category, GameRuleFactory.createIntRule(defaultValue));
    }

    static void initialize() {
        // static initialisation
    }
}
