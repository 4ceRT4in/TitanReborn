package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.shirojr.titanfabric.access.GrindstoneScreenHandlerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.screen.GrindstoneScreenHandler$4")
public abstract class GrindstoneResultSlotMixin extends Slot {
    @Unique
    private GrindstoneScreenHandler owner;

    public GrindstoneResultSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(GrindstoneScreenHandler grindstoneScreenHandler, Inventory inventory, int i, int j, int k, ScreenHandlerContext screenHandlerContext, CallbackInfo ci) {
        this.owner = grindstoneScreenHandler;
    }

    @ModifyReturnValue(method = "getExperience(Lnet/minecraft/world/World;)I", at = @At("RETURN"))
    private int getExperience(int original) {
        return ((GrindstoneScreenHandlerAccessor) owner).titanfabric$isPlatingAction() ? 0 : original;
    }

    @Inject(method = "onTakeItem", at = @At("TAIL"))
    public void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        ((GrindstoneScreenHandlerAccessor) owner).titanfabric$setPlatingAction(false);
    }
}