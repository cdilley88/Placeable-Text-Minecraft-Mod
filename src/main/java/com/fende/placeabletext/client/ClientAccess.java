package com.fende.placeabletext.client;

import com.fende.placeabletext.TextStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class ClientAccess {
    public static void openEditor(TextStyle style, BlockPos target) {
        Minecraft.getInstance().setScreen(new TextStyleScreen(style, target));
    }
    private ClientAccess() {}
}

