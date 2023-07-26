package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetherSwordItem extends TitanFabricSwordItem {
    public NetherSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, WeaponEffects.FIRE);
    }
}
