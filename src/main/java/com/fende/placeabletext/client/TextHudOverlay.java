package com.fende.placeabletext.client;

import com.fende.placeabletext.PlaceableTextMod;
import com.fende.placeabletext.item.PlacerItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class TextHudOverlay {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PlaceableTextMod.MOD_ID, "selected_character");

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !(mc.player.getMainHandItem().getItem() instanceof PlacerItem placer)) return;
        String value = placer.selectedLabel(mc.player.getMainHandItem());
        Component text = Component.translatable("gui.placeabletext.selected", value);
        int x = (graphics.guiWidth() - mc.font.width(text)) / 2;
        int y = graphics.guiHeight() - 62;
        graphics.fill(x - 5, y - 4, x + mc.font.width(text) + 5, y + 12, 0xA0000000);
        graphics.drawString(mc.font, text, x, y, 0xFFFFFF, true);
    }
    private TextHudOverlay() {}
}

