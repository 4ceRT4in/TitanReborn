package net.shirojr.titanfabric.util.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.List;
import java.util.Set;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY;
import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECT_NBT_KEY;

public class ToolTipHelper {
    private ToolTipHelper() {
        // private constructor to avoid instantiating this utility class
    }

    /**
     * Builds the ToolTip TranslationKey for the Sword ItemStack which has a TitanFabric Weapon Effect
     *
     * @param tooltip original tooltip of the ItemStack
     * @param stack   original ItemStack
     */
    public static void appendSwordToolTip(List<Text> tooltip, ItemStack stack) {
        NbtCompound baseCompound = stack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);
        Set<String> effectNbtKeys = baseCompound.getKeys();
        effectNbtKeys.stream().filter(key -> key.equals(WeaponEffectType.INNATE_EFFECT.getNbtKey()))
                .forEach(innateKey -> buildSwordToolTipTranslation(innateKey, baseCompound, tooltip, stack));
        effectNbtKeys.stream().filter(key -> key.equals(WeaponEffectType.ADDITIONAL_EFFECT.getNbtKey()))
                .forEach(additionalKey -> buildSwordToolTipTranslation(additionalKey, baseCompound, tooltip, stack));
    }

    private static void buildSwordToolTipTranslation(String nbtKey, NbtCompound baseCompound, List<Text> tooltip, ItemStack stack) {
        String effectId = baseCompound.getCompound(nbtKey).getString(EFFECT_NBT_KEY);
        WeaponEffect effect = WeaponEffect.getEffect(effectId);
        WeaponEffectType effectType = WeaponEffectType.getType(nbtKey);
        if (effect == null || effectType == null) return;
        String translation = "tooltip.titanfabric." + EffectHelper.getEffectStrength(stack, effectType);
        switch (effect) {
            case BLIND -> translation += "Blind";
            case FIRE -> translation += "Fire";
            case POISON -> translation += "Poison";
            case WEAK -> translation += "Weak";
            case WITHER -> translation += "Wither";
        }
        tooltip.add(new TranslatableText(translation));
    }
}
