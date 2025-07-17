package net.shirojr.titanfabric.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;

@Environment(EnvType.CLIENT)
public class ArrowItemRenderer extends ProjectileEntityRenderer<TitanFabricArrowEntity> {
    public ArrowItemRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(TitanFabricArrowEntity arrowEntity) {
        return arrowEntity.getTexture();
    }
}
