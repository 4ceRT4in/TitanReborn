package net.shirojr.titanfabric.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;

public class TitanFabricBlockEntities {
    public static BlockEntityType<DiamondFurnaceBlockEntity> DIAMOND_FURNACE;

    public static void registerBlockEntities() {
        DIAMOND_FURNACE = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(TitanFabric.MODID, "diamond_furnace"),
                FabricBlockEntityTypeBuilder.create(DiamondFurnaceBlockEntity::new,
                        TitanFabricBlocks.DIAMOND_FURNACE).build(null));
    }
}
