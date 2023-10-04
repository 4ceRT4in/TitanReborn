package net.shirojr.titanfabric.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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
            new Identifier(TitanFabric.MODID, "slime_item"),
            FabricEntityTypeBuilder.<TitanFabricArrowEntity>create(SpawnGroup.MISC, (type, world) ->
                            new TitanFabricArrowEntity(type, null, world, null, null)) // FIXME: values in lambda are only placeholders!
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build());

    public static void registerClient() {
        EntityRendererRegistry.register(TitanFabricEntities.ARROW_ITEM, ArrowItemRenderer::new);
    }

    public static void register() {
        LoggerUtil.devLogger("Initialize Entities");
    }
}
