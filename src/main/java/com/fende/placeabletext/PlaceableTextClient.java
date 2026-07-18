package com.fende.placeabletext;

import com.fende.placeabletext.client.TextAnchorRenderer;
import com.fende.placeabletext.client.TextHudOverlay;
import com.fende.placeabletext.item.PlacerItem;
import com.fende.placeabletext.network.CycleCharacterPayload;
import com.fende.placeabletext.registry.ModBlockEntities;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value = PlaceableTextMod.MOD_ID, dist = Dist.CLIENT)
public final class PlaceableTextClient {
    public PlaceableTextClient(IEventBus modBus) {
        modBus.addListener(this::registerRenderers);
        modBus.addListener(this::registerGuiLayers);
        NeoForge.EVENT_BUS.addListener(this::onScroll);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.TEXT_ANCHOR.get(), TextAnchorRenderer::new);
    }

    private void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(TextHudOverlay.ID, TextHudOverlay::render);
    }

    private void onScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || !mc.player.isShiftKeyDown()
                || !(mc.player.getMainHandItem().getItem() instanceof PlacerItem placer)) return;
        int delta = event.getScrollDeltaY() > 0 ? 1 : event.getScrollDeltaY() < 0 ? -1 : 0;
        if (delta == 0) return;
        placer.cycle(mc.player.getMainHandItem(), delta);
        PacketDistributor.sendToServer(new CycleCharacterPayload(delta));
        event.setCanceled(true);
    }
}
