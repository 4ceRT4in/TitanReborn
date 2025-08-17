package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin extends Item {
    public FireworkRocketItemMixin(Settings settings) {
        super(settings);
    }


    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if(!world.isClient && world.getGameRules().getBoolean(TitanFabricGamerules.DISABLE_ELYTRA_BOOSTING)) {
            cir.cancel();
        }
    }
}
