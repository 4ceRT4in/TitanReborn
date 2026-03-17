package net.shirojr.titanfabric.mixin;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.items.EnchantmentRestrictionHelper;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Final
    @Shadow
    private ScreenHandlerContext context;

    @Inject(
            method = "generateEnchantments(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;II)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void titanfabric$banUnbreakingOnTitanItems(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> filteredEntries = cir.getReturnValue();

        if (EnchantmentRestrictionHelper.shouldBanUnbreakingFromEnchantingTable(stack)) {
            filteredEntries = filteredEntries.stream()
                    .filter(entry -> !entry.enchantment.matchesKey(Enchantments.UNBREAKING))
                    .toList();
        }

        if (FireEnchantmentBanHelper.shouldRestrictGeneration(titanfabric$getContextWorld(), stack)) {
            filteredEntries = FireEnchantmentBanHelper.filterFireEnchantments(filteredEntries);
        }

        cir.setReturnValue(filteredEntries);
    }

    @Unique
    private World titanfabric$getContextWorld() {
        final World[] worldRef = new World[1];
        this.context.run((world, pos) -> worldRef[0] = world);
        return worldRef[0];
    }

    @Inject(method = "onContentChanged", at = @At("TAIL"))
    private void titanfabric$removeDeadEnchantOffersAfterFiltering(Inventory changedInventory, CallbackInfo ci) {
        if (changedInventory != this.inventory) return;

        ItemStack itemStack = changedInventory.getStack(0);
        boolean hasFiltering = EnchantmentRestrictionHelper.shouldBanUnbreakingFromEnchantingTable(itemStack)
                || FireEnchantmentBanHelper.shouldRestrictGeneration(titanfabric$getContextWorld(), itemStack);
        if (!hasFiltering) return;

        for (int i = 0; i < this.enchantmentPower.length; i++) {
            if (this.enchantmentPower[i] <= 0) continue;
            if (this.enchantmentId[i] != -1) continue;

            this.enchantmentPower[i] = 0;
            this.enchantmentLevel[i] = -1;
        }
    }
}
