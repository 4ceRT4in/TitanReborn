package net.shirojr.titanfabric.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;

public class ArrowItemRenderer extends EntityRenderer<TitanFabricArrowEntity> {
    public ArrowItemRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(TitanFabricArrowEntity entity) {
        if (entity.getEffect() != null) return entity.getTexture();
        else return new Identifier("textures/entity/projectiles/arrow.png");
    }

    @Override
    public void render(TitanFabricArrowEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.getItemStack() == null) {
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            return;
        }

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = entity.getItemStack();

        matrices.push();
        matrices.translate(1.0, 1.0, 1.0);
        matrices.scale(1.0f, 1.0f, 1.0f);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90)); //TODO: rotate using entity's velocity vector

        itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GUI, getLightLevel(entity.getWorld(), entity.getBlockPos()),
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);

        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int skyLight = world.getLightLevel(LightType.SKY, pos);
        int blockLight = world.getLightLevel(LightType.BLOCK, pos);
        return LightmapTextureManager.pack(blockLight, skyLight);
    }
}
