package net.shirojr.titanfabric.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.shirojr.titanfabric.entity.SpearEntity;

/**
 * Trident-style flight orientation while retaining the thrown ItemStack's
 * dynamic model predicates (weapon effect, strength and enchantment glint).
 */
public class SpearEntityRenderer extends EntityRenderer<SpearEntity> {
    private final ItemRenderer itemRenderer;

    public SpearEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SpearEntity spear, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        float flightYaw = MathHelper.lerp(tickDelta, spear.prevYaw, spear.getYaw());
        float flightPitch = MathHelper.lerp(tickDelta, spear.prevPitch, spear.getPitch());
        // The item sprite is diagonal (bottom-left to top-right).  Normalize that
        // diagonal first, then align its tip with the actual projectile velocity.
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(flightYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f - flightPitch));
        // Renderer-only offset: the tip remains at the hit point while the shaft
        // extends back out of the block.  Physics and the entity position stay intact.
        matrices.translate(0.0f, -0.70f, 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0f));
        matrices.scale(1.75f, 1.75f, 1.75f);
        itemRenderer.renderItem(spear.getStack(), ModelTransformationMode.GROUND, light,
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, spear.getWorld(), spear.getId());
        matrices.pop();
        super.render(spear, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SpearEntity spear) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
