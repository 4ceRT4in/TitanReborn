package net.shirojr.titanfabric.color;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TitanFabricColorProviders {
    static {
        create(TitanFabricItems.TITAN_CROSSBOW);
    }

    public static void create(Item item) {
        ColorProviderRegistry.ITEM.register((stack, textureLayer) -> {

            if (!stack.contains(TitanFabricDataComponents.CHARGED)) return -1;
            if (!stack.getOrDefault(TitanFabricDataComponents.CHARGED, false)) return -1;
            if (TitanCrossBowItem.getProjectiles(stack).isEmpty()) return -1;
            ItemStack firstProjectileStack = TitanCrossBowItem.getProjectiles(stack).get(0);
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

    public static void createBackpackTextures(String backpackType) {
        String inputPath = "C:\\Users\\pizza\\Documents\\TitanReborn\\TitanReborn\\src\\main\\resources\\assets\\titanfabric\\textures\\items\\backpack\\backpack_" + backpackType + "_base.png";
        String overlayInputPath = "C:\\Users\\pizza\\Documents\\TitanReborn\\TitanReborn\\src\\main\\resources\\assets\\titanfabric\\textures\\items\\backpack\\backpack_" + backpackType + "_overlay.png";
        String outputDirectory = "C:\\Users\\pizza\\Documents\\TitanReborn\\TitanReborn\\src\\main\\resources\\assets\\titanfabric\\textures\\items\\backpack\\";

        // HEX Codes for Backpacks | Taken From Vanilla Wool
        Map<String, Color> colors = Map.ofEntries(
                Map.entry("red", new Color(0xAC2C24)),
                Map.entry("orange", new Color(0xF97F1C)),
                Map.entry("blue", new Color(0x3E4DB2)),
                Map.entry("gray", new Color(0x474F52)),
                Map.entry("lime", new Color(0x86CC26)),
                Map.entry("pink", new Color(0xE6789C)),
                Map.entry("purple", new Color(0x802CB0)),
                Map.entry("light_blue", new Color(0x4EC5E7)),
                Map.entry("light_gray", new Color(0x9D9D97)),
                Map.entry("yellow", new Color(0xFCD132)),
                Map.entry("magenta", new Color(0xD660D1)),
                Map.entry("cyan", new Color(0x169B9C)),
                Map.entry("brown", new Color(0x7C4E2E)),
                Map.entry("green", new Color(0x658619)),
                Map.entry("black", new Color(0x191A1E)),
                Map.entry("white", new Color(0xFFFFFF))
        );

        try {
            BufferedImage base = ImageIO.read(new File(inputPath));
            BufferedImage overlay = ImageIO.read(new File(overlayInputPath));

            for (Map.Entry<String, Color> entry : colors.entrySet()) {
                String colorName = entry.getKey();
                Color overlayColor = entry.getValue();

                BufferedImage coloredImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        int grayPixel = base.getRGB(x, y);
                        Color grayColor = new Color(grayPixel, true);
                        int brightness = (grayColor.getRed() + grayColor.getGreen() + grayColor.getBlue()) / 3;
                        int red = (overlayColor.getRed() * brightness) / 255;
                        int green = (overlayColor.getGreen() * brightness) / 255;
                        int blue = (overlayColor.getBlue() * brightness) / 255;

                        Color newColor = new Color(red, green, blue, grayColor.getAlpha());
                        coloredImage.setRGB(x, y, newColor.getRGB());
                    }
                }
                Graphics2D g2d = coloredImage.createGraphics();
                g2d.drawImage(overlay, 0, 0, null);
                g2d.dispose();

                String outputPath = outputDirectory + "backpack_" + backpackType + "_" + colorName + ".png";
                ImageIO.write(coloredImage, "png", new File(outputPath));
                System.out.println("Created Texture: " + outputPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void register() {
        LoggerUtil.devLogger("initialising color providers");
    }
}
