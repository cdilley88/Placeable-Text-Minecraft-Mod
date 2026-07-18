package com.fende.placeabletext;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record TextStyle(int size, int textColor, int backgroundMode, int backgroundColor,
                        boolean outline, int outlineColor, boolean shadow, boolean phosphor,
                        boolean emitLight, int lightLevel, int rotation,
                        float horizontalOffset, float verticalOffset, float depthOffset) {
    public static final int BACKGROUND_NONE = 0;
    public static final int BACKGROUND_SEMI = 1;
    public static final int BACKGROUND_SOLID = 2;
    public static final TextStyle DEFAULT = new TextStyle(
            1, 0xFFFFFF, BACKGROUND_NONE, 0x000000, false, 0x000000,
            false, false, false, 10, 0, 0, 0, 1.0f);

    public TextStyle {
        size = Math.clamp(size, 0, 2);
        textColor &= 0xFFFFFF;
        backgroundMode = Math.clamp(backgroundMode, BACKGROUND_NONE, BACKGROUND_SOLID);
        backgroundColor &= 0xFFFFFF;
        outlineColor &= 0xFFFFFF;
        lightLevel = Math.clamp(lightLevel, 1, 15);
        rotation = Math.floorMod(rotation, 4);
        horizontalOffset = Math.clamp(horizontalOffset, -0.45f, 0.45f);
        verticalOffset = Math.clamp(verticalOffset, -0.45f, 0.45f);

        depthOffset = Math.clamp(depthOffset, 0.0f, 1.0f);
    }

    public static TextStyle fromItem(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains("PlaceableTextStyle") ? fromTag(tag.getCompound("PlaceableTextStyle")) : DEFAULT;
    }

    public void applyTo(ItemStack stack) {
        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        root.put("PlaceableTextStyle", toTag());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Size", size);
        tag.putInt("TextColor", textColor);
        tag.putInt("BackgroundMode", backgroundMode);
        tag.putInt("BackgroundColor", backgroundColor);
        tag.putBoolean("Outline", outline);
        tag.putInt("OutlineColor", outlineColor);
        tag.putBoolean("Shadow", shadow);
        tag.putBoolean("Phosphor", phosphor);
        tag.putBoolean("EmitLight", emitLight);
        tag.putInt("LightLevel", lightLevel);
        tag.putInt("Rotation", rotation);
        tag.putFloat("HorizontalOffset", horizontalOffset);
        tag.putFloat("VerticalOffset", verticalOffset);
        tag.putFloat("DepthOffset", depthOffset);
        tag.putInt("DepthModeVersion", 1);
        return tag;
    }

    public static TextStyle fromTag(CompoundTag tag) {
        int mode = tag.contains("BackgroundMode") ? tag.getInt("BackgroundMode")
                : tag.getBoolean("SolidBackground") ? BACKGROUND_SOLID : BACKGROUND_NONE;
        float depth = tag.contains("DepthOffset") ? tag.getFloat("DepthOffset") : 1.0f;
        if (!tag.contains("DepthModeVersion")) depth = depth > 0.02f ? 0.5f : 1.0f;
        return new TextStyle(
                tag.getInt("Size"), tag.getInt("TextColor"), mode, tag.getInt("BackgroundColor"),
                tag.getBoolean("Outline"), tag.getInt("OutlineColor"), tag.getBoolean("Shadow"),
                tag.getBoolean("Phosphor"), tag.getBoolean("EmitLight"),
                tag.contains("LightLevel") ? tag.getInt("LightLevel") : 10,
                tag.getInt("Rotation"), tag.getFloat("HorizontalOffset"), tag.getFloat("VerticalOffset"),
                depth);
    }
}