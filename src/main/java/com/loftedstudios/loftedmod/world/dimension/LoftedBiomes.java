package com.loftedstudios.loftedmod.world.dimension;

import com.loftedstudios.loftedmod.LoftedModMain;
import com.loftedstudios.loftedmod.world.feature.ModPlacedFeatures;
import com.loftedstudios.loftedmod.world.feature.ModPlacedFeatures.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.PlacedFeature;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoftedBiomes {
    public static final RegistryKey<Biome> LOFTED_WILDERNESS_KEY = registerKey("lofted_wilderness");
    public static final RegistryEntry<Biome> LOFTED_WILDERNESS;

    static {

        LOFTED_WILDERNESS = register(LOFTED_WILDERNESS_KEY, createLoftedWilderness());
    }

    private static RegistryKey<Biome> registerKey(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(LoftedModMain.MOD_ID, name));
    }

    private static RegistryEntry<Biome> register(RegistryKey<Biome> key, Biome biome) {
        return BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome);
    }

    private static Biome createLoftedWilderness() {
        return new Biome.Builder()
                .effects(createBiomeEffects(0xC4F2F2, 0x19E050, 0xBDE91F, 0xB9E3E4, 0x00DBFF, 0x00DBFF))
                .generationSettings(createGenerationSettings(getLoftedCarvers(), mergeFeatures(
                        generateOres(),
                        Map.of(GenerationStep.Feature.LOCAL_MODIFICATIONS, List.of(
                                ModPlacedFeatures.LAKE_WATER_SURFACE

                        ),
                        GenerationStep.Feature.FLUID_SPRINGS, List.of(

                        ),
                        GenerationStep.Feature.VEGETAL_DECORATION, List.of(
                                        ModPlacedFeatures.PATCH_LOFTED_GRASS

                        )))))
                .spawnSettings(createSpawnSettings(
                        Map.of(
                                SpawnGroup.MONSTER, List.of(
                                        new SpawnSettings.SpawnEntry(EntityType.HUSK, 100, 2, 6)
                                ),
                                SpawnGroup.CREATURE, List.of(

                                )
                        ),
                        Map.of(

                        )
                ))
                .precipitation(Biome.Precipitation.RAIN).temperature(0.5F).downfall(1)
                .category(Biome.Category.PLAINS)
                .build();
    }


    private static BiomeEffects createBiomeEffects(int skyColor, int foliageColor, int grassColor, int fogColor, int waterColor, int waterFogColor, BiomeEffects.GrassColorModifier grassModifier) {
        return new BiomeEffects.Builder()
                .skyColor(skyColor)
                .foliageColor(foliageColor)
                .grassColor(grassColor)
                .fogColor(fogColor)
                .waterColor(waterColor)
                .waterFogColor(waterFogColor)
                .grassColorModifier(grassModifier)
                .build();
    }


    private static BiomeEffects createBiomeEffects(int skyColor, int foliageColor, int grassColor, int fogColor, int waterColor, int waterFogColor) {
        return createBiomeEffects(skyColor, foliageColor, grassColor, fogColor, waterColor, waterFogColor, BiomeEffects.GrassColorModifier.NONE);
    }

    private static GenerationSettings createGenerationSettings(Map<GenerationStep.Carver, List<RegistryEntry<? extends ConfiguredCarver<?>>>> carvers, Map<GenerationStep.Feature, List<RegistryEntry<PlacedFeature>>> features) {
        var builder = new GenerationSettings.Builder();
        for (var step : GenerationStep.Carver.values()) {
            for (var carver : carvers.getOrDefault(step, List.of())) {
                builder.carver(step, carver);
            }
        }
        for (var step : GenerationStep.Feature.values()) {
            for (var feature : features.getOrDefault(step, List.of())) {
                builder.feature(step, feature);
            }
        }
        return builder.build();
    }


    private static Map<GenerationStep.Carver, List<RegistryEntry<? extends ConfiguredCarver<?>>>> getLoftedCarvers() {
        return Map.of(GenerationStep.Carver.AIR, List.of(
                ConfiguredCarvers.CAVE
                ));
    }

    @SafeVarargs
    public static <A, B> Map<A, List<B>> mergeFeatures(Map<A, List<B>>... maps){
        if(maps.length == 1){
            return maps[0];
        }

        Map<A, List<B>> result = new HashMap<>();
        for (var map : maps) {
            for (Map.Entry<A, List<B>> entry : map.entrySet()) {
                result.computeIfAbsent(entry.getKey(), (ignored)->new ArrayList<>()).addAll(entry.getValue());
            }
        }
        return result;
    }

    private record SpawnCost(
            double charge,
            double energyBudget
    ){
        static SpawnCost of(double charge, double energyBudget){
            return new SpawnCost(charge, energyBudget);
        }
    }

    private static SpawnSettings createSpawnSettings(Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawns, Map<EntityType<?>, SpawnCost> costs) {
        var builder = new SpawnSettings.Builder();
        for(var group : SpawnGroup.values()){
            for(var spawn : spawns.getOrDefault(group, List.of())){
                builder.spawn(group, spawn);
            }
        }
        for(var entry : costs.entrySet()){
            var cost = entry.getValue();
            builder.spawnCost(entry.getKey(), cost.charge(), cost.energyBudget());
        }
        return builder.build();
    }

    // Essentially just generates the ores for all biomes
    private static Map<GenerationStep.Feature, List<RegistryEntry<PlacedFeature>>> generateOres() {
        return Map.of(
                GenerationStep.Feature.UNDERGROUND_ORES, List.of(
                        ModPlacedFeatures.ORE_STONE,
                        ModPlacedFeatures.ORE_MINSTONE_COAL,
                        ModPlacedFeatures.ORE_MINSTONE_COPPER,
                        ModPlacedFeatures.ORE_MINSTONE_IRON,
                        ModPlacedFeatures.ORE_MINSTONE_GOLD,
                        ModPlacedFeatures.ORE_MINSTONE_DIAMOND,
                        ModPlacedFeatures.ORE_MINSTONE_VIBRANTITE

                )
        );
    }

    public static void registerBiomes(){
        LoftedModMain.LOGGER.debug("Registering Dimension Biomes for " + LoftedModMain.MOD_ID);
    }
}
