package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyExpressionValue(method = "doesRenderOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isOnFire()Z"))
    private boolean isOnFire(boolean original) {
        if (!((Entity) (Object) this instanceof LivingEntity livingEntity)) return original;
        return original && ArmorHelper.getEmberArmorCount(livingEntity) < 4;
    }
}
