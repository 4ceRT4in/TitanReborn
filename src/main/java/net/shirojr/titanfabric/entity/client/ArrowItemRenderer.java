package net.shirojr.titanfabric.entity.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;

public class ArrowItemRenderer extends EntityRenderer<TitanFabricArrowEntity> {
    public ArrowItemRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(TitanFabricArrowEntity entity) {
        if (entity.getEffect() != null) {
            return entity.getTexture();
        } else {
            return new Identifier("textures/entity/projectiles/arrow.png"); //FIXME: implement rendering from itemStack, provided from TitanFabricArrowEntity!
        }
    }
}
