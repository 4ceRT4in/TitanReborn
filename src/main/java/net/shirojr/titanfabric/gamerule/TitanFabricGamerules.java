package net.shirojr.titanfabric.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class TitanFabricGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> DO_HIT_COOLDOWN =
            registerBooleanGamerule("doHitCooldown", GameRules.Category.PLAYER, false);
    public static final GameRules.Key<GameRules.BooleanRule> DO_FOOD_REGENERATION =
            registerBooleanGamerule("doFoodRegeneration", GameRules.Category.PLAYER, false);

    private static GameRules.Key<GameRules.BooleanRule> registerBooleanGamerule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
    }
    public static void register() { }
}
