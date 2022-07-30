package com.loftedstudios.loftedmod.world.feature;

import com.loftedstudios.loftedmod.LoftedModMain;
import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import com.loftedstudios.loftedmod.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {

    public static final RuleTest LOFTED_BASE_STONE = new TagMatchRuleTest(ModTags.Blocks.LOFTED_BASE_STONE);


    // Vegetation
    public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_LOFTED_GRASS = ConfiguredFeatures.register("patch_lofted_grass", Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlockStateProvider.of(LoftedBlocks.LOFTED_FERN), 32));
    // Ores
    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_STONE = ConfiguredFeatures.register("ore_stone", Feature.ORE, oreConfig(Blocks.STONE, 45));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_MINSTONE_COAL = ConfiguredFeatures.register("ore_minstone_coal", Feature.ORE, oreConfig(LoftedBlocks.MINSTONE_COAL_ORE, 17));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_MINSTONE_COPPER = ConfiguredFeatures.register("ore_minstone_copper", Feature.ORE, oreConfig(LoftedBlocks.MINSTONE_COPPER_ORE, 12));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_MINSTONE_IRON = ConfiguredFeatures.register("ore_minstone_iron", Feature.ORE, oreConfig(LoftedBlocks.MINSTONE_IRON_ORE, 7));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_MINSTONE_GOLD = ConfiguredFeatures.register("ore_minstone_gold", Feature.ORE, oreConfig(LoftedBlocks.MINSTONE_GOLD_ORE, 9));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_MINSTONE_DIAMOND= ConfiguredFeatures.register("ore_minstone_diamond", Feature.ORE, oreConfig(LoftedBlocks.MINSTONE_DIAMOND_ORE, 5));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_MINSTONE_VIBRANTITE = ConfiguredFeatures.register("ore_minstone_vibrantite", Feature.ORE, oreConfig(LoftedBlocks.MINSTONE_VIBRANTITE_ORE, 9));






    public static void registerConfiguredFeatures(){
        LoftedModMain.LOGGER.debug("Registering ConfiguredFeatures for " + LoftedModMain.MOD_ID);
    }

    private static RandomPatchFeatureConfig createRandomPatchFeatureConfig(BlockStateProvider block, int tries) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(tries, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(block)));
    }

    private static OreFeatureConfig oreConfig(Block block, int size){
        return new OreFeatureConfig(List.of(OreFeatureConfig.createTarget(new BlockMatchRuleTest(LoftedBlocks.MINSTONE), block.getDefaultState())), size);
    }



}
