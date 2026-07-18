package com.fende.placeabletext.registry;

import com.fende.placeabletext.PlaceableTextMod;
import com.fende.placeabletext.block.TextAnchorBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PlaceableTextMod.MOD_ID);
    public static final DeferredBlock<TextAnchorBlock> TEXT_ANCHOR =
            BLOCKS.register("text_anchor", TextAnchorBlock::new);
    private ModBlocks() {}
}

