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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiamondSwordItem extends TitanFabricSwordItem {
    public DiamondSwordItem(boolean isGreatsword, ToolMaterial toolMaterial, int attackDamage, float attackSpeed) {
        super(isGreatsword, toolMaterial, attackDamage, attackSpeed, null);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("tooltip.titanfabric.diamond_greatsword"));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, EffectHelper.generateAllEffectVersionStacks(this, stacks));
    }
}
