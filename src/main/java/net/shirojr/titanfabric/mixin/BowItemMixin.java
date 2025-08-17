package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricTags;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(BowItem.class)
public abstract class BowItemMixin implements SelectableArrow {

    @Shadow public abstract int getMaxUseTime(ItemStack stack, LivingEntity user);

    @Inject(method = "getProjectiles", at = @At("HEAD"), cancellable = true)
    private void titanfabric$getProjectiles(CallbackInfoReturnable<Predicate<ItemStack>> cir) {
        BowItem bowItem = (BowItem) (Object) this;
        if (!(bowItem instanceof SelectableArrow weaponWithSelectableArrow)) return;
        Predicate<ItemStack> validArrowItem = stack -> {
            for (Item arrow : weaponWithSelectableArrow.titanFabric$supportedArrows()) {
                if (stack.getItem().equals(arrow)) return true;
            }
            return BowItem.BOW_PROJECTILES.test(stack);
        };
        cir.setReturnValue(validArrowItem);
    }

    @WrapOperation(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getProjectileType(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack titanFabric$getProjectileTypeWrap(PlayerEntity instance, ItemStack stack, Operation<ItemStack> original) {
        ItemStack originalResult = original.call(instance, stack);
        if (originalResult.isEmpty() && instance.getAbilities().creativeMode) {
            return new ItemStack(Items.ARROW);
        }
        return originalResult;
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void titanFabric$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient()) ArrowSelectionHelper.cleanUpProjectileSelection(user, user.getStackInHand(hand));
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return Registries.ITEM.stream().filter(item -> item.getDefaultStack().isIn(TitanFabricTags.Items.DEFAULT_ARROWS)).toList();
    }
}
