package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.FirePlacementHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({FlintAndSteelItem.class, FireChargeItem.class})
public abstract class FirePlacementItemMixin {
    @WrapOperation(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos titanfabric$targetReplaceablePlant(
            BlockPos clickedPos,
            Direction side,
            Operation<BlockPos> original,
            ItemUsageContext context
    ) {
        return FirePlacementHelper.getPlacementPos(context.getWorld(), clickedPos, side);
    }

    @WrapOperation(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractFireBlock;canPlaceAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"
            )
    )
    private boolean titanfabric$useBucketPlacementBehavior(
            World world,
            BlockPos pos,
            Direction direction,
            Operation<Boolean> original
    ) {
        return original.call(world, pos, direction)
                || FirePlacementHelper.prepareNormalFirePlacement(world, pos);
    }
}
