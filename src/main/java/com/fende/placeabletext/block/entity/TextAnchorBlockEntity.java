package com.fende.placeabletext.block.entity;

import com.fende.placeabletext.TextStyle;
import com.fende.placeabletext.item.PlacerItem;
import com.fende.placeabletext.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public final class TextAnchorBlockEntity extends BlockEntity {
    private String character = "A";
    private PlacerItem.Category category = PlacerItem.Category.LETTER;
    private TextStyle style = TextStyle.DEFAULT;
    private Direction surface = Direction.NORTH;
    private UUID owner;

    public TextAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TEXT_ANCHOR.get(), pos, state);
    }

    public void initialize(String character, PlacerItem.Category category, TextStyle style, Direction surface, UUID owner) {
        this.character = character;
        this.category = category;
        this.style = style;
        this.surface = surface;
        this.owner = owner;
        sync();
    }

    public String character() { return character; }
    public PlacerItem.Category category() { return category; }
    public TextStyle style() { return style; }
    public Direction surface() { return surface; }

    public void setStyle(TextStyle style) { this.style = style; sync(); }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.getLightEngine().checkBlock(worldPosition);
        }
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Character", character);
        tag.putString("Category", category.name());
        tag.put("Style", style.toTag());
        tag.putString("Surface", surface.getSerializedName());
        if (owner != null) tag.putUUID("Owner", owner);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        character = tag.getString("Character");
        try { category = PlacerItem.Category.valueOf(tag.getString("Category")); } catch (IllegalArgumentException ignored) {}
        style = tag.contains("Style") ? TextStyle.fromTag(tag.getCompound("Style")) : TextStyle.DEFAULT;
        surface = Direction.byName(tag.getString("Surface"));
        if (surface == null) surface = Direction.NORTH;
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) loadAdditional(tag, registries);
    }
}

