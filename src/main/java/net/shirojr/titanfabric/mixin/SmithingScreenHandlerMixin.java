package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.EssenceCrafting;
import net.shirojr.titanfabric.util.items.MultiBowHelper;
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

        if (productStack.getItem() instanceof MultiBowItem multiStackProduct &&
                essenceStack.getItem() instanceof MultiBowItem multiStackEssence) {
            int productArrows = multiStackProduct.getFullArrowCount();
            int essenceArrows = multiStackEssence.getFullArrowCount();

            if (productArrows == essenceArrows) {
                int newMaxArrowCount = productArrows + 1;
                if (newMaxArrowCount <= 3) {
                    ItemStack outputStack;
                    switch (newMaxArrowCount) {
                        case 2 -> outputStack = new ItemStack(TitanFabricItems.MULTI_BOW_2);
                        case 3 -> outputStack = new ItemStack(TitanFabricItems.MULTI_BOW_3);
                        default -> outputStack = new ItemStack(TitanFabricItems.MULTI_BOW_1);
                    }

                    outputStack.setNbt(productStack.getNbt());
                    MultiBowHelper.setFullArrowCount(outputStack, newMaxArrowCount);

                    output.setStack(0, outputStack);
                    ci.cancel();
                    return;
                } else output.setStack(0, Items.AIR.getDefaultStack());
            } else output.setStack(0, Items.AIR.getDefaultStack());
        }

        if (!titanfabric$essenceCraftingItemsInSlots(productStack, essenceStack)) return;

        ItemStack outputStack = productStack.copy();
        if (EffectHelper.stackHasWeaponEffect(productStack.getOrCreateNbt())) {
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

        if (titanfabric$essenceCraftingItemsInSlots(productStack, essenceStack)) {
            cir.setReturnValue(true);
            return;
        }
        if (titanfabric$multibowCraftingItemsInSlots(productStack, essenceStack)) {
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(false);
    }

    @Unique
    private boolean titanfabric$essenceCraftingItemsInSlots(ItemStack productStack, ItemStack ingredientStack) {
        if (!(productStack.getItem() instanceof EssenceCrafting essenceProduct) ||
                !essenceProduct.isType().equals(EssenceCrafting.ItemType.PRODUCT)) return false;
        if (!(ingredientStack.getItem() instanceof EssenceCrafting essenceIngredient) ||
                !essenceIngredient.isType().equals(EssenceCrafting.ItemType.INGREDIENT)) return false;

        WeaponEffects ingredientEffect = ((EssenceCrafting) ingredientStack.getItem()).ingredientEffect(ingredientStack);
        return ingredientEffect != null;
    }

    @Unique
    private boolean titanfabric$multibowCraftingItemsInSlots(ItemStack productStack, ItemStack ingredientStack) {
        if (!(productStack.getItem() instanceof MultiBowItem multiProductItem)) return false;
        if (!(ingredientStack.getItem() instanceof MultiBowItem multiIngredientItem)) return false;

        if (multiProductItem.getFullArrowCount() != multiIngredientItem.getFullArrowCount()) return false;
        return multiProductItem.getFullArrowCount() <= 3;
    }
}
