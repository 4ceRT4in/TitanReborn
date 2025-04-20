package net.shirojr.titanfabric.color;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.DyeColor;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class TitanFabricColorProviders {
    static {
        create(TitanFabricItems.CUT_POTION);
        create(TitanFabricItems.TITAN_CROSSBOW);
    }

    public static void create(Item item) {
        ColorProviderRegistry.ITEM.register((stack, textureLayer) -> {
            NbtCompound stackNbt = stack.getOrCreateNbt();

            if (!stackNbt.contains("Charged")) return -1;
            if (!stackNbt.getBoolean("Charged")) return -1;
            if (TitanCrossBowItem.getProjectiles(stack).size() < 1) return -1;
            ItemStack firstProjectileStack = TitanCrossBowItem.getProjectiles(stack).get(0);
            if (WeaponEffectData.fromNbt(firstProjectileStack.getOrCreateNbt(), WeaponEffectType.INNATE_EFFECT).isPresent()) {
                WeaponEffectData weaponEffectData = WeaponEffectData.fromNbt(firstProjectileStack.getOrCreateNbt(), WeaponEffectType.INNATE_EFFECT).get();
                if (weaponEffectData.weaponEffect().getColor() != -1) return weaponEffectData.weaponEffect().getColor();
            }
            if (firstProjectileStack.getItem() instanceof PotionItem) {
                if (textureLayer == 0) return PotionUtil.getColor(firstProjectileStack);
            }
            return -1;
        }, item);
    }

    public static void register() {
        LoggerUtil.devLogger("initialising color providers");
    }
}
