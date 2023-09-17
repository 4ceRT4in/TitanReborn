package net.shirojr.titanfabric.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.custom.*;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;

public class TitanFabricBlocks {

    public static final Block CITRIN_ORE = registerBlock("citrin_ore",
            new CitrinOreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(4.5f).strength(5.0f).requiresTool()), TitanFabricItemGroups.TITAN);
    public static final Block NETHER_ORE = registerBlock("nether_ore",
            new NetherOreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(4.5f).strength(6.0f).requiresTool()), TitanFabricItemGroups.TITAN);
    public static final Block DEEPSTALE_LEGEND_ORE = registerBlock("deepslate_legend_ore",
            new LegendOreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(10.0f).strength(7.0f).requiresTool()), TitanFabricItemGroups.TITAN);

    public static final Block CITRIN_BLOCK = registerBlock("citrin_block",
            new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).mapColor(DyeColor.YELLOW)
                    .hardness(12.0f).strength(6.0f).requiresTool()), TitanFabricItemGroups.TITAN);
    public static final Block NETHER_BLOCK = registerBlock("nether_block",
            new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).mapColor(DyeColor.RED)
                    .hardness(12.0f).strength(6.0f).requiresTool()), TitanFabricItemGroups.TITAN);
    public static final Block LEGEND_BLOCK = registerBlock("legend_block",
            new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).mapColor(DyeColor.PURPLE)
                    .hardness(12.0f).strength(6.0f).requiresTool()), TitanFabricItemGroups.TITAN);

    public static final Block DIRT_LIGHT = registerBlock("dirt_light",
            new Block(FabricBlockSettings.of(Material.SOIL).sounds(BlockSoundGroup.GRAVEL).mapColor(DyeColor.BROWN)
                    .hardness(0.5f).strength(0.5f).luminance(15)), TitanFabricItemGroups.TITAN);
    public static final Block STONE_LIGHT = registerBlock("stone_light",
            new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(1.5f).strength(6.0f).luminance(15).requiresTool()), TitanFabricItemGroups.TITAN);

    public static final Block DIAMOND_FURNACE = registerBlock("diamond_furnace",
            new DiamondFurnaceBlock(), TitanFabricItemGroups.TITAN);

    public static final Block NETHERITE_ANVIL = registerBlock("netherite_anvil",
            new AdvancedAnvilBlock(FabricBlockSettings.of(Material.METAL).nonOpaque()), TitanFabricItemGroups.TITAN);


    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(TitanFabric.MODID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(TitanFabric.MODID, name),
                new BlockItem(block, new FabricItemSettings().group(group)));
    }

    private static Block registerBlockWithoutItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.BLOCK, new Identifier(TitanFabric.MODID, name), block);
    }

    public static void registerModBlocks() {
        TitanFabric.devLogger("Registering " + TitanFabric.MODID + " Mod blocks");
    }
}
