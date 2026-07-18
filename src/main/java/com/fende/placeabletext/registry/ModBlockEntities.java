package com.fende.placeabletext.registry;

import com.fende.placeabletext.PlaceableTextMod;
import com.fende.placeabletext.block.entity.TextAnchorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PlaceableTextMod.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TextAnchorBlockEntity>> TEXT_ANCHOR =
            BLOCK_ENTITIES.register("text_anchor", () -> BlockEntityType.Builder.of(
                    TextAnchorBlockEntity::new, ModBlocks.TEXT_ANCHOR.get()).build(null));
    private ModBlockEntities() {}
}

