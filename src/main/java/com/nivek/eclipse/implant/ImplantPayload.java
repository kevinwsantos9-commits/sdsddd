package com.nivek.eclipse.implant;

import com.nivek.eclipse.NivekEclipse;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: alternar (instalar/remover) o implante do slot (indice do enum ImplantSlot). */
public record ImplantPayload(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImplantPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "implant_toggle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ImplantPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ImplantPayload::slot, ImplantPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
