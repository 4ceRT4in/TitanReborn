package net.shirojr.titanfabric.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;

public class TitanFabricBlockEntities {
    public static BlockEntityType<DiamondFurnaceBlockEntity> DIAMOND_FURNACE;

    public static void registerBlockEntities() {
        DIAMOND_FURNACE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(TitanFabric.MODID, "diamond_furnace"),
                FabricBlockEntityTypeBuilder.create(DiamondFurnaceBlockEntity::new,
                        TitanFabricBlocks.DIAMOND_FURNACE).build(null));
    }
}
