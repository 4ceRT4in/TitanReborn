package net.shirojr.titanfabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.server.world.ServerWorld;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    @Inject(method = "processStacks", at = @At("RETURN"), cancellable = true)
    private static void titanfabric$sanitizeLootStacks(
            ServerWorld world,
            Consumer<ItemStack> consumer,
            CallbackInfoReturnable<Consumer<ItemStack>> cir
    ) {
        if (!FireEnchantmentBanHelper.isFireEnchantmentBanEnabled(world)) return;

        Consumer<ItemStack> original = cir.getReturnValue();
        cir.setReturnValue(stack -> original.accept(FireEnchantmentBanHelper.getSanitizedLootStack(world, stack)));
    }
}
