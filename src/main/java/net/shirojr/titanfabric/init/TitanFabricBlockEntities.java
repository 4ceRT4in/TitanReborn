package net.shirojr.titanfabric.init;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;

public interface TitanFabricBlockEntities {
    BlockEntityType<DiamondFurnaceBlockEntity> DIAMOND_FURNACE = register(
            "diamond_furnace",
            DiamondFurnaceBlockEntity::new,
            TitanFabricBlocks.DIAMOND_FURNACE
    );

    @SuppressWarnings("SameParameterValue")
    private static <T extends BlockEntity> BlockEntityType<T> register(String name,
                                                                       BlockEntityType.BlockEntityFactory<? extends T> entityFactory,
                                                                       Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, TitanFabric.getId(name),
                BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    static void initialize() {
        // static initialisation
    }
}
