package com.fende.placeabletext.registry;

import com.fende.placeabletext.block.TextAnchorBlock;
import com.fende.placeabletext.block.entity.TextAnchorBlockEntity;
import com.fende.placeabletext.item.PlacerItem;
import com.fende.placeabletext.network.CycleCharacterPayload;
import com.fende.placeabletext.network.UpdateStylePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToServer(CycleCharacterPayload.TYPE, CycleCharacterPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            ItemStack held = context.player().getMainHandItem();
                            if (held.getItem() instanceof PlacerItem placer) placer.cycle(held, payload.delta());
                        }))
                .playToServer(UpdateStylePayload.TYPE, UpdateStylePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (!(context.player() instanceof ServerPlayer player)) return;
                            if (payload.target() == Long.MIN_VALUE) {
                                ItemStack held = player.getMainHandItem();
                                if (held.getItem() instanceof PlacerItem) payload.style().applyTo(held);
                                return;
                            }
                            BlockPos pos = BlockPos.of(payload.target());
                            if (player.blockPosition().distSqr(pos) > 64) return;
                            if (player.level().getBlockEntity(pos) instanceof TextAnchorBlockEntity text) {
                                text.setStyle(payload.style());
                                int light = payload.style().emitLight() ? payload.style().lightLevel() : 0;
                                player.level().setBlock(pos, text.getBlockState().setValue(TextAnchorBlock.LIGHT, light), 3);
                            }
                        }));
    }
    private ModNetworking() {}
}
