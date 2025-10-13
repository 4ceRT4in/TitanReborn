package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashPotionItem.class)
public abstract class SplashPotionItemMixin extends PotionItem {

    private SplashPotionItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("RETURN"))
    private void onUse(World world, PlayerEntity user, Hand hand,
                       CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        // Add a cooldown to prevent splash damage/health from becoming too overpowered when stackable
        user.getItemCooldownManager().set(this, 10);
    }
}
