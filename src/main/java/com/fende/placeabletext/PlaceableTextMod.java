package com.fende.placeabletext;

import com.fende.placeabletext.registry.ModBlockEntities;
import com.fende.placeabletext.registry.ModBlocks;
import com.fende.placeabletext.registry.ModItems;
import com.fende.placeabletext.registry.ModNetworking;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(PlaceableTextMod.MOD_ID)
public final class PlaceableTextMod {
    public static final String MOD_ID = "placeabletext";

    public PlaceableTextMod(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        modBus.addListener(ModNetworking::register);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.LETTER_PLACER.get());
            event.accept(ModItems.NUMBER_PLACER.get());
            event.accept(ModItems.SPECIAL_PLACER.get());
        }
    }
}

