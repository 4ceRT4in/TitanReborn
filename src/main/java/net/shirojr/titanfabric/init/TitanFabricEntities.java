package net.shirojr.titanfabric.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.entity.CitrinStarEntity;
import net.shirojr.titanfabric.entity.TitanFabricArrowEntity;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricEntities {
    public static final EntityType<TitanFabricArrowEntity> ARROW_ITEM = register("arrow",
            EntityType.Builder.<TitanFabricArrowEntity>create(TitanFabricArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .build()
    );

    public static final EntityType<CitrinStarEntity> CITRIN_STAR = register("citrin_star",
            EntityType.Builder.<CitrinStarEntity>create(CitrinStarEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .build()
    );

    private static <E extends Entity, T extends EntityType<E>> T register(String name, T entityType) {
        return Registry.register(Registries.ENTITY_TYPE, TitanFabric.getId(name), entityType);
    }


    public static void register() {
        LoggerUtil.devLogger("Initialize Entities");
    }
}
