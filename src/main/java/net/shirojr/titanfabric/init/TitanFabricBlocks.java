package net.shirojr.titanfabric.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.custom.AdvancedAnvilBlock;
import net.shirojr.titanfabric.block.custom.DiamondFurnaceBlock;
import net.shirojr.titanfabric.block.custom.TitanFabricOreBlock;
import net.shirojr.titanfabric.util.LoggerUtil;

@SuppressWarnings("unused")
public interface TitanFabricBlocks {

    Block LEGEND_CRYSTAL = registerBlock("legend_crystal",
            new AmethystClusterBlock(7, 3, AbstractBlock.Settings.create().nonOpaque().ticksRandomly().sounds(BlockSoundGroup.AMETHYST_CLUSTER)
                    .strength(3.5F).luminance(state -> 10).requiresTool()));
    Block CITRIN_ORE = registerBlock("citrin_ore",
            new TitanFabricOreBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY)
                    .hardness(4.5f).strength(3.0f, 3.0f).requiresTool(), 1, 3));
    Block EMBER_ORE = registerBlock("ember_ore",
            new TitanFabricOreBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.NETHER_ORE).mapColor(DyeColor.RED)
                    .hardness(4.5f).strength(3.5f, 3.0f).requiresTool(), 1, 4));
    Block DEEPSTALE_LEGEND_ORE = registerBlock("deepslate_legend_ore",
            new TitanFabricOreBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.DEEPSLATE).mapColor(MapColor.DEEPSLATE_GRAY)
                    .hardness(10.0f).strength(4.5f, 3.0f).requiresTool(), 1, 7));

    Block CITRIN_BLOCK = registerBlock("citrin_block",
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(DyeColor.YELLOW)
                    .hardness(12.0f).strength(6.0f).requiresTool()));
    Block EMBER_BLOCK = registerBlock("ember_block",
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(DyeColor.RED)
                    .hardness(12.0f).strength(6.0f).requiresTool()));
    Block LEGEND_BLOCK = registerBlock("legend_block",
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(DyeColor.PURPLE)
                    .hardness(12.0f).strength(6.0f).requiresTool()));

    Block DIAMOND_FURNACE = registerBlock("diamond_furnace",
            new DiamondFurnaceBlock(AbstractBlock.Settings.create().mapColor(DyeColor.CYAN)
                    .requiresTool().strength(3.5f)));

    Block NETHERITE_ANVIL = registerBlock("netherite_anvil",
            new AdvancedAnvilBlock(AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).nonOpaque()
                    .requiresTool().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.ANVIL)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, TitanFabric.getId(name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Item registeredEntry = Registry.register(Registries.ITEM, TitanFabric.getId(name), new BlockItem(block, new Item.Settings()));
        TitanFabricItems.ALL_ITEMS.add(new ItemStack(registeredEntry));
    }

    static void registerModBlocks() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MOD_ID + " Mod blocks");
    }
}
