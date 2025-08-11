package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.titanfabric.access.AnvilScreenHandlerAccessor;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import net.shirojr.titanfabric.util.items.Anvilable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler implements AnvilScreenHandlerAccessor {
    @Shadow
    @Final
    private Property levelCost;
    @Unique private boolean isNetherite;
    @Unique private boolean requiresNetherite;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("HEAD"))
    private void cacheAnvilType(CallbackInfo ci) {
        context.run((world, pos) -> isNetherite = world.getBlockState(pos).isOf(TitanFabricBlocks.NETHERITE_ANVIL));
        requiresNetherite = false;
    }

    @ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z", ordinal = 1))
    private boolean titanfabric$modifyIsDamageableCheck(boolean original) {
        ItemStack itemStack = this.input.getStack(0).copy();
        return original || itemStack.getItem() instanceof Anvilable;
    }

    @Inject(method = "updateResult", at = @At("RETURN"))
    private void updateResultReturn(CallbackInfo ci) {
        if (!isNetherite) {
            ItemStack result = this.output.getStack(0);
            if (!result.isEmpty() && EnchantmentHelper.canHaveEnchantments(result)) {
                if(OverpoweredEnchantmentsHelper.isOverpowered(result)) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    requiresNetherite = true;
                    this.sendContentUpdates();
                }
            }
        }
        requiresNetherite = false;
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    private void titanfabric$canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        boolean ok = (player.isCreative() || player.experienceLevel >= levelCost.get()) && levelCost.get() > 0;
        cir.setReturnValue(ok);
    }

    @WrapOperation(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private void titanFabric$changedWorldEventHandlerForAnvil(ScreenHandlerContext ctx, BiConsumer<World, BlockPos> fn, Operation<Void> original) {
        if (!isNetherite) {
            original.call(ctx, fn);
        } else {
            context.run((world, pos) -> player.getWorld().playSound(null, pos, SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.BLOCKS, 1f, 1f));
        }
    }

    @WrapOperation(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 4))
    private void reduceXpCost(Property instance, int originalXp, Operation<Void> original) {
        int xp = isNetherite ? Math.max(1, originalXp/2) : originalXp;
        original.call(instance, xp);
    }

    @Inject(method = "updateResult", at = @At("RETURN"))
    private void updateResult(CallbackInfo ci) {
        int raw = levelCost.get();
        int display = isNetherite ? Math.max(0, raw/2) : raw;
        levelCost.set(display);
    }

    @ModifyReturnValue(method = "getLevelCost", at = @At("RETURN"))
    private int getLevelCost(int original) {
        return isNetherite ? Math.max(0, original/2) : original;
    }

    @Override
    public boolean titanfabric$requiresNetherite() {
        return requiresNetherite;
    }
}
