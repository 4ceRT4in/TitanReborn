package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArrowEntityRenderer.class)
public abstract class ArrowEntityRendererMixin {
    @ModifyReturnValue(method = "getTexture(Lnet/minecraft/entity/projectile/ArrowEntity;)Lnet/minecraft/util/Identifier;", at = @At("RETURN"))
    private Identifier titanfabric$ChangeArrowEntitySprites(Identifier original, ArrowEntity arrowEntity) {
        if (!(arrowEntity instanceof TitanFabricArrowEntity titanFabricArrowEntity)) {
            return original;
        }

        // Proceed with the texture if TitanFabricArrowEntity has a valid effect and texture
        return titanFabricArrowEntity.getEffect().isPresent() ? titanFabricArrowEntity.getTexture() : original;
    }
}


