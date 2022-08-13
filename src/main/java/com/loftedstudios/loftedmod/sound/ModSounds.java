package com.loftedstudios.loftedmod.sound;

import com.loftedstudios.loftedmod.LoftedModMain;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSounds {

    public static SoundEvent BLIMP_DESTROY = registerSoundEvent("blimp_destroy");
    public static SoundEvent BLIMP_CREAK = registerSoundEvent("blimp_creak");

    private static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(LoftedModMain.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerModSounds() {
        LoftedModMain.LOGGER.info("Registering sounds for " + LoftedModMain.MOD_ID);
    }
}
