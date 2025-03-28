package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
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
import java.util.function.BiConsumer;

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

    @WrapOperation(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private void titanFabric$changedWorldEventHandlerForAnvil(ScreenHandlerContext instance, BiConsumer<World, BlockPos> function, Operation<Void> original) {
        Optional<BlockState> blockState = context.get(World::getBlockState);
        if (blockState.isEmpty() || !blockState.get().isOf(TitanFabricBlocks.NETHERITE_ANVIL)) {
            original.call(instance, function);
            return;
        }
        context.run((world, pos) -> {
            if (!player.getAbilities().creativeMode &&
                    blockState.get().isIn(BlockTags.ANVIL) &&
                    player.getRandom().nextFloat() < 0.12F) {
                BlockState landingBlockState = AnvilBlock.getLandingState(blockState.get());
                if (landingBlockState == null) {
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
                } else {
                    world.setBlockState(pos, landingBlockState, Block.NOTIFY_LISTENERS);
                    anvilUseHandler(pos);
                    // world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                }
            } else {
                anvilUseHandler(pos);
                //world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
            }
            //TODO: avoiding world events like that might have more things connected than only the sound usage.
            //      If anything is missing in-game, implement it in the C2S packet handler
        });
    }

    @Unique
    private void anvilUseHandler(BlockPos pos) {
        player.getWorld().playSound(null, pos, SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.BLOCKS, 1f, 1f);
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
