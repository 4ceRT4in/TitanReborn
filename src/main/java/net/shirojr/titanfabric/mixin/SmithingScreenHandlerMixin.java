package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.EssenceCrafting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
    private void titanfabric$weaponEffectCraftingResultUpdate(CallbackInfo ci) {
        ItemStack productStack = input.getStack(0);
        ItemStack essenceStack = input.getStack(1);

        if (!titanfabric$correctItemsInSlots(productStack, essenceStack)) return;

        ItemStack outputStack = productStack.copy();
        if (EffectHelper.stackHasWeaponEffect(productStack)) {
            EffectHelper.setEffectStrength(outputStack, EffectHelper.getEffectStrength(outputStack) + 1);
        } else EffectHelper.setEffectStrength(outputStack, 1);

        //TODO: handle weaponeffect applying
        output.setStack(0, outputStack);
        ci.cancel();
    }

    @Inject(method = "canTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void titanfabric$weaponEffectCraftingResultUnlock(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ItemStack productStack = input.getStack(0);
        ItemStack essenceStack = input.getStack(1);

        if (!titanfabric$correctItemsInSlots(productStack, essenceStack)) return;
        cir.setReturnValue(true);
    }

    @Unique
    private boolean titanfabric$correctItemsInSlots(ItemStack productStack, ItemStack ingredientStack) {
        if (!(productStack.getItem() instanceof EssenceCrafting essenceProduct) ||
                !essenceProduct.isType().equals(EssenceCrafting.ItemType.PRODUCT)) return false;
        if (!(ingredientStack.getItem() instanceof EssenceCrafting essenceIngredient) ||
                !essenceIngredient.isType().equals(EssenceCrafting.ItemType.INGREDIENT)) return false;

        WeaponEffects ingredientEffect = ((EssenceCrafting) ingredientStack.getItem()).ingredientEffect();
        return ingredientEffect != null;
    }
}
