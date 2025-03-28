package net.shirojr.titanfabric.render.model;

import com.google.common.collect.Sets;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

import java.util.Set;

public class TitanFabricModelLayers {
    private static final String MAIN = "main";
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();
    public static final EntityModelLayer PARACHUTE = registerMain("parachute");

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = create(id, layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        }
        return entityModelLayer;
    }

    private static EntityModelLayer registerMain(String id) {
        return register(id, MAIN);
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(new Identifier(TitanFabric.MOD_ID, id), layer);
    }
}
