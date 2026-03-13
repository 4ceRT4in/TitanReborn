package net.shirojr.titanfabric.mixin;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.shirojr.titanfabric.util.items.EnchantmentRestrictionHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {
    @Final
    @Shadow
    private Inventory inventory;

    @Final
    @Shadow
    public int[] enchantmentPower;

    @Final
    @Shadow
    public int[] enchantmentId;

    @Final
    @Shadow
    public int[] enchantmentLevel;

    @Inject(
            method = "generateEnchantments(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;II)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void titanfabric$banUnbreakingOnTitanItems(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (!EnchantmentRestrictionHelper.shouldBanUnbreakingFromEnchantingTable(stack)) return;

        List<EnchantmentLevelEntry> filteredEntries = cir.getReturnValue().stream()
                .filter(entry -> !entry.enchantment.matchesKey(Enchantments.UNBREAKING))
                .toList();
        cir.setReturnValue(filteredEntries);
    }

    @Inject(method = "onContentChanged", at = @At("TAIL"))
    private void titanfabric$removeDeadEnchantOffersAfterFiltering(Inventory changedInventory, CallbackInfo ci) {
        if (changedInventory != this.inventory) return;

        ItemStack itemStack = changedInventory.getStack(0);
        if (!EnchantmentRestrictionHelper.shouldBanUnbreakingFromEnchantingTable(itemStack)) return;

        for (int i = 0; i < this.enchantmentPower.length; i++) {
            if (this.enchantmentPower[i] <= 0) continue;
            if (this.enchantmentId[i] != -1) continue;

            this.enchantmentPower[i] = 0;
            this.enchantmentLevel[i] = -1;
        }
    }
}
