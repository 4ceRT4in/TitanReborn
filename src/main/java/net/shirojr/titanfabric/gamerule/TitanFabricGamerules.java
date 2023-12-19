package net.shirojr.titanfabric.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class TitanFabricGamerules {

    public static final GameRules.Key<GameRules.BooleanRule> DO_HIT_COOLDOWN = registerBooleanGamerule("legacyCombat", GameRules.Category.PLAYER, false);
    public static final GameRules.Key<GameRules.BooleanRule> LEGACY_FOOD_REGENERATION = registerBooleanGamerule("legacyFoodRegeneration", GameRules.Category.PLAYER, true);

    private static GameRules.Key<GameRules.BooleanRule> registerBooleanGamerule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    public static void register() {
    }
}
