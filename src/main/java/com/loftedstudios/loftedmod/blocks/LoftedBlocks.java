package com.loftedstudios.loftedmod.blocks;

import com.loftedstudios.loftedmod.LoftedModMain;
import com.loftedstudios.loftedmod.blocks.custom.BranchBlock;
import com.loftedstudios.loftedmod.blocks.custom.LoftedFernBlock;
import com.loftedstudios.loftedmod.blocks.custom.LoftedGrassBlock;
import com.loftedstudios.loftedmod.item.LoftedItemGroup;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LoftedBlocks {

    // Stone Blocks

    public static final Block MINSTONE = registerBlock("minstone",
            new Block(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_COAL_ORE = registerBlock("minstone_coal_ore",
            new OreBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_COPPER_ORE = registerBlock("minstone_copper_ore",
            new OreBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_IRON_ORE = registerBlock("minstone_iron_ore",
            new OreBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_GOLD_ORE = registerBlock("minstone_gold_ore",
            new OreBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_DIAMOND_ORE = registerBlock("minstone_diamond_ore",
            new OreBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_VIBRANTITE_ORE = registerBlock("minstone_vibrantite_ore",
            new OreBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_STAIRS = registerBlock("minstone_stairs",
            new StairsBlock(MINSTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool().mapColor(MapColor.STONE_GRAY)),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block MINSTONE_SLAB = registerBlock("minstone_slab",
            new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool().mapColor(MapColor.STONE_GRAY)),
            LoftedItemGroup.LOFTED_ITEMGROUP);


    public static final Block COBBLED_MINSTONE = registerBlock("cobbled_minstone",
            new Block(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()),
            LoftedItemGroup.LOFTED_ITEMGROUP);



    //Blocks

    public static final Block LOFTED_GRASS = registerBlock("lofted_grass",
            new LoftedGrassBlock(AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK)),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block FEYWOOD_BRANCH = registerBlock("feywood_branch", new BranchBlock(0.5f, FabricBlockSettings.of(Material.LEAVES).strength(0.5f, 0.2f).mapColor(MapColor.BROWN)), LoftedItemGroup.LOFTED_ITEMGROUP);

    public static final Block COPPER_SHINGLES = registerBlock("copper_shingles",
            new StairsBlock(Blocks.WAXED_COPPER_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(3.0f, 6.0f).requiresTool().mapColor(MapColor.ORANGE)),
            LoftedItemGroup.LOFTED_ITEMGROUP);

    // Vegetation
    public static final Block LOFTED_FERN = registerBlock("lofted_fern",
            new LoftedFernBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS)), ItemGroup.DECORATIONS);

    // Wood
    public static final Block FEYWOOD_LOG = registerBlock("feywood_log",
            new PillarBlock(FabricBlockSettings.copy(Blocks.OAK_LOG).strength(4.0f).requiresTool()), LoftedItemGroup.LOFTED_ITEMGROUP);




    //Registry

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(LoftedModMain.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(group)));
    }

    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(LoftedModMain.MOD_ID, name), block);
    }



    public static void registerModBlocks() {
        LoftedModMain.LOGGER.info("Registering blocks for " + LoftedModMain.MOD_ID);
    }
}
