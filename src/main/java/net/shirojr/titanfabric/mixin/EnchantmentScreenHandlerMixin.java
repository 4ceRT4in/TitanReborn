package net.shirojr.titanfabric.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.items.EnchantmentRestrictionHelper;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import net.shirojr.titanfabric.item.custom.spear.TitanFabricSpearItem;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {
    @Final
    @Shadow
    private ScreenHandlerContext context;

    @ModifyArgs(
            method = "generateEnchantments(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;II)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;generateEnchantments(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;"
            )
    )
    private void titanfabric$excludeBlockedEnchantmentsBeforeSelection(Args args) {
        ItemStack stack = args.get(1);
        Stream<RegistryEntry<Enchantment>> possibleEnchantments = args.get(3);
        World world = titanfabric$getContextWorld();
        boolean banUnbreaking = EnchantmentRestrictionHelper.shouldBanUnbreakingFromEnchantingTable(stack);
        boolean banFireEnchantments = FireEnchantmentBanHelper.shouldRestrictGeneration(world, stack);
        boolean isSpear = stack.getItem() instanceof TitanFabricSpearItem;

        args.set(3, possibleEnchantments.filter(enchantment ->
                (!banUnbreaking || !enchantment.matchesKey(Enchantments.UNBREAKING))
                        && (!banFireEnchantments || !FireEnchantmentBanHelper.isFireEnchantment(enchantment))
                        && (!isSpear || !enchantment.matchesKey(Enchantments.FIRE_ASPECT))
        ));
    }

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

        filteredEntries = filteredEntries.stream()
                .filter(entry -> !(stack.getItem() instanceof TitanFabricSpearItem)
                        || !entry.enchantment.matchesKey(Enchantments.FIRE_ASPECT))
                .map(EnchantmentScreenHandlerMixin::titanfabric$capTableOnlyLevel)
                .toList();

        cir.setReturnValue(filteredEntries);
    }

    @Unique
    private World titanfabric$getContextWorld() {
        final World[] worldRef = new World[1];
        this.context.run((world, pos) -> worldRef[0] = world);
        return worldRef[0];
    }

    @Unique
    private static EnchantmentLevelEntry titanfabric$capTableOnlyLevel(EnchantmentLevelEntry entry) {
        int maximum = OverpoweredEnchantmentsHelper.getNormalMaximum(entry.enchantment);
        return entry.level <= maximum ? entry : new EnchantmentLevelEntry(entry.enchantment, maximum);
    }
}
