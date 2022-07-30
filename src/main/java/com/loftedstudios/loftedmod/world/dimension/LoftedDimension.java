package com.loftedstudios.loftedmod.world.dimension;

import com.loftedstudios.loftedmod.LoftedModMain;
import com.loftedstudios.loftedmod.util.MiscUtil;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.*;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.io.IOException;

public class LoftedDimension {
    public static final RegistryKey<World> LOFTED_WORLD_KEY = RegistryKey.of(Registry.WORLD_KEY, new Identifier(LoftedModMain.MOD_ID, "lofteddim"));
    public static final RegistryKey<DimensionType> DIMENSION_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, LOFTED_WORLD_KEY.getValue());


    public static void register() {
        LoftedModMain.LOGGER.debug("Registering ModDimensions for " + LoftedModMain.MOD_ID);
    }
}
