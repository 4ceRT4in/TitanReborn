package net.shirojr.titanfabric.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.entity.client.ArrowItemRenderer;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricEntities {

    public static final EntityType<TitanFabricArrowEntity> ARROW_ITEM = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(TitanFabric.MODID, "arrow_item"),
            FabricEntityTypeBuilder.<TitanFabricArrowEntity>create(SpawnGroup.MISC, (entityType, world) ->
                            new TitanFabricArrowEntity(world)).dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeBlocks(4).trackedUpdateRate(20).build());
    public static final EntityType<CitrinStarEntity> CITRIN_STAR = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(TitanFabric.MODID, "citrin_star"),
            FabricEntityTypeBuilder.<CitrinStarEntity>create(SpawnGroup.MISC, (entityType, world) ->
                    new CitrinStarEntity(world)).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    public static void registerClient() {
        EntityRendererRegistry.register(ARROW_ITEM, ArrowItemRenderer::new);
        EntityRendererRegistry.register(CITRIN_STAR, FlyingItemEntityRenderer::new);
    }

    public static void register() {
        LoggerUtil.devLogger("Initialize Entities");
    }
}
