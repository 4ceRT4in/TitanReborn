package net.shirojr.titanfabric.mixin;

import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricTags;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin implements SelectableArrow {
    @Inject(method = "use", at = @At("HEAD"))
    private void titanFabric$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient()) ArrowSelectionHelper.cleanUpProjectileSelection(user, user.getStackInHand(hand));
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return Registries.ITEM.stream().filter(item -> item.getDefaultStack().isIn(TitanFabricTags.Items.DEFAULT_CROSSBOW_ARROWS)).toList();
    }

    @Inject(method = "getSpeed", at = @At("HEAD"), cancellable = true)
    private static void getPotionSpeed(ChargedProjectilesComponent stack, CallbackInfoReturnable<Float> cir) {
        for (ItemStack projectile : stack.getProjectiles()) {
            Item projectileItem = projectile.getItem();
            if (projectileItem instanceof PotionItem) {
                cir.setReturnValue(TitanCrossBowItem.getPotionSpeed());
                return;
            }
        }
    }
}
