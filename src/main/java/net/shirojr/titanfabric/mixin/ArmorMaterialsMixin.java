package net.shirojr.titanfabric.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterials;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialsMixin {

    @Shadow
    @Mutable
    @Final
    private int durabilityMultiplier;

    @Shadow
    @Mutable
    @Final
    private String name;

    @Inject(method = "getDurability", at = @At("HEAD"))
    private void titanfabric$getDurabilityMixin(EquipmentSlot slot, CallbackInfoReturnable<Integer> ci) {
        if (this.name.equals("netherite")) {
            this.durabilityMultiplier = 74;
        }
    }
}
