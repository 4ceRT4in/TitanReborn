package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public interface TitanFabricGamerules {
    GameRules.Key<GameRules.BooleanRule> LEGACY_COMBAT = registerBooleanGamerule("TitanFabric.legacyCombat", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> LEGACY_ABSORPTION = registerBooleanGamerule("TitanFabric.legacyAbsorption", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> LEGACY_FOOD_REGENERATION = registerBooleanGamerule("TitanFabric.legacyFoodRegeneration", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> DISABLE_ELYTRA_BOOSTING = registerBooleanGamerule("TitanFabric.disableElytraBoosting", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> ADVANCED_FROSTBURN_THAWING = registerBooleanGamerule("TitanFabric.advancedFrostburnThawing", GameRules.Category.MISC, false);
    GameRules.Key<GameRules.IntRule> HOT_BLOCK_SEARCH_RANGE = registerIntegerGamerule("TitanFabric.hotBlockSearchRange", GameRules.Category.MISC, 7, -1, null);
    GameRules.Key<GameRules.IntRule> HOT_BLOCK_AMOUNT_FOR_THAWING = registerIntegerGamerule("TitanFabric.hotBlockAmountForThawing", GameRules.Category.MISC, 1, 1, null);
    GameRules.Key<GameRules.BooleanRule> GREATSWORD_COOLDOWN = registerBooleanGamerule("TitanFabric.greatSwordCooldown", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> FULL_INVENTORY_POTION_BAG_SEARCH = registerBooleanGamerule("TitanFabric.fullInventoryPotionBagSearch", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> SOUL_FIRE_INFINITE = registerBooleanGamerule("TitanFabric.doInfiniteSoulFireBurn", GameRules.Category.PLAYER, false);

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
