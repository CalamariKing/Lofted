package com.loftedstudios.loftedmod.entities.client;

import com.loftedstudios.loftedmod.LoftedModMain;
import com.loftedstudios.loftedmod.entities.AirShipEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class AirShipEntityModel extends AnimatedGeoModel<AirShipEntity> {

    @Override
    public Identifier getModelLocation(AirShipEntity object) {
        return new Identifier(LoftedModMain.MOD_ID, "geo/airship.geo.json");
    }

    @Override
    public Identifier getTextureLocation(AirShipEntity object) {
        return new Identifier(LoftedModMain.MOD_ID, "textures/entity/red_airship.png");
    }

    @Override
    public Identifier getAnimationFileLocation(AirShipEntity animatable) {
        return new Identifier(LoftedModMain.MOD_ID, "animations/airship.animation.json");
    }
}