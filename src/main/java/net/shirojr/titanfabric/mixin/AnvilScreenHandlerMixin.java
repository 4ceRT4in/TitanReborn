package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.world.World;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.items.Anvilable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z", ordinal = 1))
    private boolean titanfabric$anvilProductIsDamageable(ItemStack instance) {
        return instance.isDamageable() || instance.getItem() instanceof Anvilable;
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    private void titanfabric$canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        boolean original = (player.isCreative() || player.experienceLevel >= levelCost.get());
        original = original && this.levelCost.get() > 0;
        cir.setReturnValue(original);
    }

    @Redirect(method = "updateResult",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCustomName(Lnet/minecraft/text/Text;)Lnet/minecraft/item/ItemStack;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 5)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V")
    )
    private void titanfabric$modifyXpCost(Property instance, int i) {
        LoggerUtil.devLogger("used anvil redirect for xp");
        Optional<BlockState> blockState = context.get(World::getBlockState);
        int xp = calculateXp(blockState.orElse(null), i);
        instance.set(xp);
    }

    @ModifyReturnValue(method = "getLevelCost", at = @At("RETURN"))
    private int titanfabric$reducedCostForNetheriteAnvil(int original) {
        Optional<BlockState> blockState = context.get(World::getBlockState);
        return calculateXp(blockState.orElse(null), original);
    }

    @Unique
    private int calculateXp(@Nullable BlockState state, int originalXp) {
        if (state == null || !state.isOf(TitanFabricBlocks.NETHERITE_ANVIL)) return originalXp;
        return Math.max(1, originalXp / 2);

    }
}
