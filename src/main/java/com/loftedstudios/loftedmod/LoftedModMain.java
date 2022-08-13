package com.loftedstudios.loftedmod;

import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import com.loftedstudios.loftedmod.entities.ModEntityTypes;
import com.loftedstudios.loftedmod.item.ModItems;
import com.loftedstudios.loftedmod.sound.ModSounds;
import com.loftedstudios.loftedmod.util.ModRegistries;
import com.loftedstudios.loftedmod.world.dimension.LoftedBiomes;
import com.loftedstudios.loftedmod.world.dimension.LoftedDimension;
import com.loftedstudios.loftedmod.world.feature.ModConfiguredFeatures;
import com.loftedstudios.loftedmod.world.feature.ModPlacedFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.GeckoLib;

public class LoftedModMain implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "loftedmod";
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModConfiguredFeatures.registerConfiguredFeatures();
		ModPlacedFeatures.registerPlacedFeatures();
		ModItems.registerModItems();
		LoftedBlocks.registerModBlocks();
		ModRegistries.registerModStuffs();
		ModSounds.registerModSounds();
		LoftedBiomes.registerBiomes();
		GeckoLib.initialize();
		LoftedDimension.register();


	}

}
