package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class ThrownPotionEntityMixin {

    @Inject(method = "applyLingeringPotion", at = @At("HEAD"))
    private void onApplyLingeringPotion(ItemStack stack, Potion potion, CallbackInfo ci) {
        // Cast the AreaEffectCloudEntity and modify its radius before it is spawned
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(((PotionEntity) (Object) this).world, ((PotionEntity) (Object) this).getX(), ((PotionEntity) (Object) this).getY(), ((PotionEntity) (Object) this).getZ());
        // Set a larger radius
        areaEffectCloudEntity.setRadius(1F);  // Example larger radius value
    }
}