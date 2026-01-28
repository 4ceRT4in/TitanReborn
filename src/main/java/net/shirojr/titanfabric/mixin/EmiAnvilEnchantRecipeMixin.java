package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.recipe.special.EmiAnvilEnchantRecipe;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EmiAnvilEnchantRecipe.class)
public class EmiAnvilEnchantRecipeMixin {

//    @WrapOperation(method = "<init>", at = @At(value = "FIELD", target = "Ldev/emi/emi/recipe/special/EmiAnvilEnchantRecipe;level:I"))
//    private void initMixin(EmiAnvilEnchantRecipe instance, int value, Operation<Void> original, Item tool, Enchantment enchantment, int level, Identifier id) {
//        String enchantmentId = EmiPort.getEnchantmentRegistry().getEntry(enchantment).getIdAsString();
//        if (enchantmentId.equals("minecraft:power") || enchantmentId.equals("minecraft:sharpness") || enchantmentId.equals("minecraft:protection")) {
//            original.call(instance, value - 1);
//        } else {
//            original.call(instance, value);
//        }
//    }

    @WrapOperation(method = "getTool",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/registry/entry/RegistryEntry;I)V"))
    private void getToolMixin(ItemStack instance, RegistryEntry<Enchantment> enchantment, int level, Operation<Void> original){
        if (enchantment.getIdAsString().equals("minecraft:power") || enchantment.getIdAsString().equals("minecraft:sharpness") || enchantment.getIdAsString().equals("minecraft:protection")) {
            original.call(instance, enchantment, level - 1);
        } else {
            original.call(instance, enchantment, level);
        }
    }

    @WrapOperation(method = "getBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/ItemEnchantmentsComponent$Builder;add(Lnet/minecraft/registry/entry/RegistryEntry;I)V"))
    private void getBookMixin(ItemEnchantmentsComponent.Builder instance, RegistryEntry<Enchantment> enchantment, int level, Operation<Void> original) {
        if (enchantment.getIdAsString().equals("minecraft:power") || enchantment.getIdAsString().equals("minecraft:sharpness") || enchantment.getIdAsString().equals("minecraft:protection")) {
            original.call(instance, enchantment, level - 1);
        } else {
            original.call(instance, enchantment, level);
        }
    }
}
