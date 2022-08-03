package com.loftedstudios.loftedmod.entities;

import com.loftedstudios.loftedmod.LoftedModMain;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntityTypes {
    public static final EntityType<AirShipEntity> AIRSHIP = Registry.register(Registry.ENTITY_TYPE, new Identifier(LoftedModMain.MOD_ID, "airship"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, AirShipEntity::new).
            dimensions(EntityDimensions.fixed(1.375f, 0.5625f)).trackRangeBlocks(10).build());






}

