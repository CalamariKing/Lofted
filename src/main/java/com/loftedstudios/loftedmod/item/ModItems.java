package com.loftedstudios.loftedmod.item;

import com.loftedstudios.loftedmod.LoftedModMain;
import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import com.loftedstudios.loftedmod.entities.AirShipEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final AirShipItem RED_AIRSHIP = new AirShipItem(AirShipEntity.Type.RED, new FabricItemSettings().group(LoftedItemGroup.LOFTED_ITEMGROUP).maxCount(1));
    public static final AirShipItem YELLOW_AIRSHIP = new AirShipItem(AirShipEntity.Type.YELLOW, new FabricItemSettings().group(LoftedItemGroup.LOFTED_ITEMGROUP).maxCount(1));
    public static final AirShipItem BLACK_AIRSHIP = new AirShipItem(AirShipEntity.Type.BLACK, new FabricItemSettings().group(LoftedItemGroup.LOFTED_ITEMGROUP).maxCount(1));
    public static final AirShipItem BLUE_AIRSHIP = new AirShipItem(AirShipEntity.Type.BLUE, new FabricItemSettings().group(LoftedItemGroup.LOFTED_ITEMGROUP).maxCount(1));

    public static final Item FEYWOOD_SIGN = new SignItem(new FabricItemSettings().group(LoftedItemGroup.LOFTED_ITEMGROUP).maxCount(16), LoftedBlocks.FEYWOOD_SIGN_BLOCK, LoftedBlocks.FEYWOOD_WALL_SIGN_BLOCK);

    public static final Item VIBRANTITE_CHUNK = new Item(new FabricItemSettings().group(LoftedItemGroup.LOFTED_ITEMGROUP).maxCount(64));



    public static void registerModItems() {
        Registry.register(Registry.ITEM, new Identifier(LoftedModMain.MOD_ID, "red_airship"), RED_AIRSHIP);
        Registry.register(Registry.ITEM, new Identifier(LoftedModMain.MOD_ID, "yellow_airship"), YELLOW_AIRSHIP);
        Registry.register(Registry.ITEM, new Identifier(LoftedModMain.MOD_ID, "black_airship"), BLACK_AIRSHIP);
        Registry.register(Registry.ITEM, new Identifier(LoftedModMain.MOD_ID, "blue_airship"), BLUE_AIRSHIP);

        Registry.register(Registry.ITEM, new Identifier(LoftedModMain.MOD_ID, "feywood_sign"), FEYWOOD_SIGN);

        Registry.register(Registry.ITEM, new Identifier(LoftedModMain.MOD_ID, "vibrantite_chunk"), VIBRANTITE_CHUNK);
        LoftedModMain.LOGGER.info("Registering Mod Items for " + LoftedModMain.MOD_ID);
    }
}
