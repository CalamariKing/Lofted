package com.loftedstudios.loftedmod.world.feature;

import com.loftedstudios.loftedmod.LoftedModMain;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {

    // Vegetation
    public static final RegistryEntry<PlacedFeature> PATCH_LOFTED_GRASS = register("patch_lofted_grass", ModConfiguredFeatures.PATCH_LOFTED_GRASS, CountPlacementModifier.of(10), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.aboveBottom(3), YOffset.belowTop(0)), HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING), BiomePlacementModifier.of());

    //Ores
    public static final RegistryEntry<PlacedFeature> ORE_STONE = PlacedFeatures.register("ore_stone", ModConfiguredFeatures.ORE_STONE, CountPlacementModifier.of(40), SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(48), YOffset.aboveBottom(384)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_MINSTONE_COAL = PlacedFeatures.register("ore_minstone_coal", ModConfiguredFeatures.ORE_MINSTONE_COAL, CountPlacementModifier.of(30), SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(260), YOffset.getTop()), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_MINSTONE_COPPER = PlacedFeatures.register("ore_minstone_copper", ModConfiguredFeatures.ORE_MINSTONE_COPPER, CountPlacementModifier.of(16), SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(40), YOffset.aboveBottom(240)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_MINSTONE_IRON = PlacedFeatures.register("ore_minstone_iron", ModConfiguredFeatures.ORE_MINSTONE_IRON, CountPlacementModifier.of(12), SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(40), YOffset.aboveBottom(200)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_MINSTONE_GOLD = PlacedFeatures.register("ore_minstone_gold", ModConfiguredFeatures.ORE_MINSTONE_GOLD, CountPlacementModifier.of(4), SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(40), YOffset.aboveBottom(150)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_MINSTONE_DIAMOND = PlacedFeatures.register("ore_minstone_diamond", ModConfiguredFeatures.ORE_MINSTONE_DIAMOND, CountPlacementModifier.of(4), SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(0), YOffset.aboveBottom(140)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_MINSTONE_VIBRANTITE = PlacedFeatures.register("ore_minstone_vibrantite", ModConfiguredFeatures.ORE_MINSTONE_VIBRANTITE, CountPlacementModifier.of(20), SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(150), YOffset.aboveBottom(300)), BiomePlacementModifier.of());

    //Misc
        public static final RegistryEntry<PlacedFeature> LAKE_WATER_SURFACE = PlacedFeatures.register("lake_water_surface", ModConfiguredFeatures.WATER_LAKE, RarityFilterPlacementModifier.of(90), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());





    static RegistryEntry<PlacedFeature> register(String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> feature, PlacementModifier... modifiers) {
        return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, new Identifier(LoftedModMain.MOD_ID, id), new PlacedFeature(RegistryEntry.upcast(feature), List.of(modifiers)));
    }

    public static void registerPlacedFeatures(){
        LoftedModMain.LOGGER.debug("Registering PlayedFeatures for " + LoftedModMain.MOD_ID);
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }
}
