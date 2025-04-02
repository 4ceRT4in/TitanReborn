package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.TitanFabricTags;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(BowItem.class)
public class BowItemMixin implements SelectableArrows {
    @Inject(method = "getProjectiles", at = @At("HEAD"), cancellable = true)
    private void titanfabric$getProjectiles(CallbackInfoReturnable<Predicate<ItemStack>> cir) {
        BowItem bowItem = (BowItem) (Object) this;
        if (!(bowItem instanceof SelectableArrows weaponWithSelectableArrows)) return;
        Predicate<ItemStack> validArrowItem = stack -> {
            for (Item arrow : weaponWithSelectableArrows.titanFabric$supportedArrows()) {
                if (stack.getItem().equals(arrow)) return true;
            }
            return BowItem.BOW_PROJECTILES.test(stack);
        };
        cir.setReturnValue(validArrowItem);
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void titanFabric$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient()) ArrowSelectionHelper.cleanUpProjectileSelection(user, this);
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return Registries.ITEM.stream().filter(item -> item.getDefaultStack().isIn(TitanFabricTags.Items.DEFAULT_ARROWS)).toList();
    }
}
