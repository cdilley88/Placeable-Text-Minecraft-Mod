package com.fende.placeabletext.network;

import com.fende.placeabletext.PlaceableTextMod;
import com.fende.placeabletext.TextStyle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateStylePayload(long target, TextStyle style) implements CustomPacketPayload {
    public static final Type<UpdateStylePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PlaceableTextMod.MOD_ID, "update_style"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateStylePayload> STREAM_CODEC =
            StreamCodec.of(UpdateStylePayload::write, UpdateStylePayload::read);

    private static void write(RegistryFriendlyByteBuf b, UpdateStylePayload p) {
        TextStyle s = p.style;
        b.writeLong(p.target); b.writeVarInt(s.size()); b.writeInt(s.textColor());
        b.writeVarInt(s.backgroundMode()); b.writeInt(s.backgroundColor());
        b.writeBoolean(s.outline()); b.writeInt(s.outlineColor()); b.writeBoolean(s.shadow());
        b.writeBoolean(s.phosphor()); b.writeBoolean(s.emitLight()); b.writeVarInt(s.lightLevel());
        b.writeVarInt(s.rotation()); b.writeFloat(s.horizontalOffset());
        b.writeFloat(s.verticalOffset()); b.writeFloat(s.depthOffset());
    }

    private static UpdateStylePayload read(RegistryFriendlyByteBuf b) {
        long target = b.readLong();
        return new UpdateStylePayload(target, new TextStyle(
                b.readVarInt(), b.readInt(), b.readVarInt(), b.readInt(), b.readBoolean(),
                b.readInt(), b.readBoolean(), b.readBoolean(), b.readBoolean(), b.readVarInt(),
                b.readVarInt(), b.readFloat(), b.readFloat(), b.readFloat()));
    }
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

