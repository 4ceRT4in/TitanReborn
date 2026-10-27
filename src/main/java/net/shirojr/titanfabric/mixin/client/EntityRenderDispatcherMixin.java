package net.shirojr.titanfabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.access.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Unique
    private static final SpriteIdentifier TEXTURE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/soul_fire_0"));
    @Unique
    private static final SpriteIdentifier TEXTURE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/soul_fire_1"));

    @WrapOperation(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;", ordinal = 1))
    private Sprite getSprite1(SpriteIdentifier obj, Operation<Sprite> original, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
        if (titanfabric$shouldRenderSoulFire(entity)) {
            return TEXTURE_1.getSprite();
        }
        return original.call(obj);
    }

    @WrapOperation(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;", ordinal = 0))
    private Sprite getSprite0(SpriteIdentifier obj, Operation<Sprite> original, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
        if (titanfabric$shouldRenderSoulFire(entity)) {
            return TEXTURE_0.getSprite();
        }
        return original.call(obj);
    }

    @Unique
    private boolean titanfabric$shouldRenderSoulFire(Entity entity) {
        return ((EntityAccessor) entity).titanfabric$isSoulBurning();
    }
}
