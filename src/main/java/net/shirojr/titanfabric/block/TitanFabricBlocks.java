package net.shirojr.titanfabric.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.custom.*;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricBlocks {

    public static final Block CITRIN_ORE = registerBlock("citrin_ore",
            new TitanFabricOreBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(4.5f).strength(3.0f, 3.0f).requiresTool(), 3));
    public static final Block EMBER_ORE = registerBlock("ember_ore",
            new TitanFabricOreBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(4.5f).strength(3.5f, 3.0f).requiresTool(), 4));
    public static final Block DEEPSTALE_LEGEND_ORE = registerBlock("deepslate_legend_ore",
            new TitanFabricOreBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(10.0f).strength(4.5f, 3.0f).requiresTool(), 7));

    public static final Block CITRIN_BLOCK = registerBlock("citrin_block",
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(DyeColor.YELLOW)
                    .hardness(12.0f).strength(6.0f).requiresTool()));
    public static final Block EMBER_BLOCK = registerBlock("ember_block",
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(DyeColor.RED)
                    .hardness(12.0f).strength(6.0f).requiresTool()));
    public static final Block LEGEND_BLOCK = registerBlock("legend_block",
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(DyeColor.PURPLE)
                    .hardness(12.0f).strength(6.0f).requiresTool()));

    public static final Block DIAMOND_FURNACE = registerBlock("diamond_furnace",
            new DiamondFurnaceBlock(AbstractBlock.Settings.create().mapColor(DyeColor.CYAN)
                    .requiresTool().strength(3.5f)));

    public static final Block NETHERITE_ANVIL = registerBlock("netherite_anvil",
            new AdvancedAnvilBlock(AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).nonOpaque()
                    .requiresTool().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.ANVIL)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(TitanFabric.MODID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(TitanFabric.MODID, name),
                new BlockItem(block, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    }

    public static void registerModBlocks() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod blocks");
    }
}
