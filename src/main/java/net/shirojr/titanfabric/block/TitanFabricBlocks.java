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
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricBlocks {

    public static final Block CITRIN_ORE = registerBlock("citrin_ore",
            new TitanFabricOreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(4.5f).strength(3.0f, 3.0f).requiresTool(), 3), TitanFabricItemGroups.TITAN);
    public static final Block EMBER_ORE = registerBlock("ember_ore",
            new TitanFabricOreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(4.5f).strength(3.5f, 3.0f).requiresTool(), 4), TitanFabricItemGroups.TITAN);
    public static final Block DEEPSTALE_LEGEND_ORE = registerBlock("deepslate_legend_ore",
            new TitanFabricOreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(10.0f).strength(4.5f, 3.0f).requiresTool(), 7), TitanFabricItemGroups.TITAN);

    public static final Block CITRIN_BLOCK = registerBlock("citrin_block",
            new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).mapColor(DyeColor.YELLOW)
                    .hardness(12.0f).strength(6.0f).requiresTool()), TitanFabricItemGroups.TITAN);
    public static final Block EMBER_BLOCK = registerBlock("ember_block",
            new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).mapColor(DyeColor.RED)
                    .hardness(12.0f).strength(6.0f).requiresTool()), TitanFabricItemGroups.TITAN);
    public static final Block LEGEND_BLOCK = registerBlock("legend_block",
            new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).mapColor(DyeColor.PURPLE)
                    .hardness(12.0f).strength(6.0f).requiresTool()), TitanFabricItemGroups.TITAN);

    public static final Block DIAMOND_FURNACE = registerBlock("diamond_furnace",
            new DiamondFurnaceBlock(FabricBlockSettings.of(Material.METAL).mapColor(DyeColor.CYAN)
                    .requiresTool().strength(3.5f)), TitanFabricItemGroups.TITAN);

    public static final Block NETHERITE_ANVIL = registerBlock("netherite_anvil",
            new AdvancedAnvilBlock(FabricBlockSettings.of(Material.REPAIR_STATION).mapColor(DyeColor.BLACK).nonOpaque()
                    .requiresTool().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.ANVIL)), TitanFabricItemGroups.TITAN);


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
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod blocks");
    }
}
