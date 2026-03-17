package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.shirojr.titanfabric.network.packet.ArmorHudOverlayPacket;
import net.shirojr.titanfabric.network.packet.DisableSwimmingPacket;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public interface TitanFabricGamerules {
    GameRules.Key<GameRules.BooleanRule> LEGACY_COMBAT = registerBooleanGamerule("TitanFabric.legacyCombat", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> LEGACY_ABSORPTION = registerBooleanGamerule("TitanFabric.legacyAbsorption", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> LEGACY_FOOD_REGENERATION = registerBooleanGamerule("TitanFabric.legacyFoodRegeneration", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> DISABLE_ELYTRA_BOOSTING = registerBooleanGamerule("TitanFabric.disableElytraBoosting", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> GREATSWORD_COOLDOWN = registerBooleanGamerule("TitanFabric.greatSwordCooldown", GameRules.Category.PLAYER, true);
    GameRules.Key<GameRules.BooleanRule> ARMOR_HUD_OVERLAY = registerBooleanGamerule(
            "titanfabric.ArmorHudOverlay",
            GameRules.Category.PLAYER,
            true,
            (server, rule) -> {
                boolean enabled = rule.get();
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    new ArmorHudOverlayPacket(enabled).sendPacket(player);
                }
            }
    );
    GameRules.Key<GameRules.BooleanRule> DISABLE_SWIMMING = registerBooleanGamerule(
            "titanfabric.DisableSwimming",
            GameRules.Category.PLAYER,
            true,
            (server, rule) -> {
                boolean enabled = rule.get();
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    new DisableSwimmingPacket(enabled).sendPacket(player);
                }
            }
    );
    GameRules.Key<GameRules.BooleanRule> DISABLE_FIRE_ENCHANTMENTS = registerBooleanGamerule(
            "titanfabric.DisableFireEnchantments",
            GameRules.Category.PLAYER,
            true
    );
    @SuppressWarnings("SameParameterValue")
    private static GameRules.Key<GameRules.BooleanRule> registerBooleanGamerule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    @SuppressWarnings("SameParameterValue")
    private static GameRules.Key<GameRules.BooleanRule> registerBooleanGamerule(
            String name, GameRules.Category category, boolean defaultValue,
            BiConsumer<MinecraftServer, GameRules.BooleanRule> callback) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue, callback));
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

    static void syncArmorHudOverlay(ServerPlayerEntity player) {
        boolean enabled = player.getWorld().getGameRules().getBoolean(ARMOR_HUD_OVERLAY);
        new ArmorHudOverlayPacket(enabled).sendPacket(player);
    }

    static void syncDisableSwimming(ServerPlayerEntity player) {
        boolean enabled = player.getWorld().getGameRules().getBoolean(DISABLE_SWIMMING);
        new DisableSwimmingPacket(enabled).sendPacket(player);
    }
}
