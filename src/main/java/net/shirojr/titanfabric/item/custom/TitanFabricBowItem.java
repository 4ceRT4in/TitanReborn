package net.shirojr.titanfabric.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrow;

import java.util.function.Predicate;

public abstract class TitanFabricBowItem extends BowItem implements SelectableArrow {

    public TitanFabricBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> {
            for (Item supportedArrowItem : this.titanFabric$supportedArrows()) {
                if (stack.getItem().equals(supportedArrowItem)) {
                    return true;
                }
            }
            return stack.isIn(ItemTags.ARROWS);
        };
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) ArrowSelectionHelper.cleanUpProjectileSelection(user, user.getStackInHand(hand));
        return super.use(world, user, hand);
    }
}
