package com.fende.placeabletext.network;

import com.fende.placeabletext.PlaceableTextMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CycleCharacterPayload(int delta) implements CustomPacketPayload {
    public static final Type<CycleCharacterPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PlaceableTextMod.MOD_ID, "cycle_character"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleCharacterPayload> STREAM_CODEC =
            StreamCodec.of((buf, p) -> buf.writeVarInt(p.delta), buf -> new CycleCharacterPayload(buf.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
