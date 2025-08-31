package net.shirojr.titanfabric.color;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

public class TitanFabricColorProviders {
    static {
        create(TitanFabricItems.TITAN_CROSSBOW);
    }

    public static void create(Item item) {
        ColorProviderRegistry.ITEM.register((stack, textureLayer) -> {
            ChargedProjectilesComponent loadedProjectiles = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
            if (loadedProjectiles == null || loadedProjectiles.isEmpty()) return -1;
            ItemStack firstProjectileStack = loadedProjectiles.getProjectiles().get(0);
            if (WeaponEffectData.get(firstProjectileStack, WeaponEffectType.INNATE_EFFECT).isPresent()) {
                WeaponEffectData weaponEffectData = WeaponEffectData.get(firstProjectileStack, WeaponEffectType.INNATE_EFFECT).get();
                if (weaponEffectData.weaponEffect().getColor() != -1) return weaponEffectData.weaponEffect().getColor();
            }
            if (firstProjectileStack.contains(DataComponentTypes.POTION_CONTENTS)) {
                var potionComponent = firstProjectileStack.get(DataComponentTypes.POTION_CONTENTS);
                if (textureLayer == 0 && potionComponent != null) return potionComponent.getColor();
            }
            return -1;
        }, item);
    }

    public static void register() {
        LoggerUtil.devLogger("initialising color providers");
    }
}
