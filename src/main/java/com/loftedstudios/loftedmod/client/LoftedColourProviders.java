package com.loftedstudios.loftedmod.client;


import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;

@Environment(EnvType.CLIENT)
public class LoftedColourProviders {

    public static void registerColourProviders(){
        ColorProviderRegistryImpl.BLOCK.register(((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D)), LoftedBlocks.LOFTED_GRASS);
    }
}
