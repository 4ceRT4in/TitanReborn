package net.shirojr.titanfabric.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.List;
import java.util.function.Predicate;

public abstract class TitanFabricBowItem extends BowItem implements SelectableArrows {

    public TitanFabricBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> {
            for (Item supportedArrowItem : this.supportedArrows()) {
                if (stack.getItem().equals(supportedArrowItem)) {
                    return true;
                }
            }
            return stack.isIn(ItemTags.ARROWS);
        };
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ArrowSelectionHandler arrowSelection = (ArrowSelectionHandler) user;
        if (arrowSelection.titanfabric$getSelectedArrow().isEmpty())
            return super.use(world, user, hand);
        ItemStack selectedArrowStack = arrowSelection.titanfabric$getSelectedArrow().get();
        if (ArrowSelectionHelper.containsArrowStack(selectedArrowStack, user.getInventory(), this))
            return super.use(world, user, hand);

        List<ItemStack> selectableArrows = ArrowSelectionHelper.findAllSupportedArrowStacks(user.getInventory(), this);
        ItemStack newSelectedStack = null;
        if (selectableArrows.size() > 0) newSelectedStack = selectableArrows.get(0);
        arrowSelection.titanfabric$setSelectedArrow(newSelectedStack);

        return TypedActionResult.success(user.getStackInHand(hand), false);
    }
}
