package net.shirojr.titanfabric.util.items;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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
        HashSet<WeaponEffectData> effectData = stack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (effectData == null || effectData.isEmpty()) return;
        for (WeaponEffectData entry : effectData.stream()
                .filter(e -> e.type() != WeaponEffectType.INNATE_EFFECT)
                .sorted(Comparator.comparing(WeaponEffectData::type))
                .toList()) {
            if (entry == null || entry.weaponEffect() == null) continue;
            String translation = "tooltip.titanfabric." + EffectHelper.getEffectStrength(stack, entry.type());
            switch (entry.weaponEffect()) {
                case BLIND -> translation += "Blind";
                case FIRE -> translation += "Fire";
                case POISON -> translation += "Poison";
                case WEAK -> translation += "Weak";
                case WITHER -> translation += "Wither";
            }
            tooltip.add(Text.translatable(translation));
        }
    }
}
