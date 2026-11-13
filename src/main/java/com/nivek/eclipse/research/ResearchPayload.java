package com.nivek.eclipse.research;

import com.nivek.eclipse.NivekEclipse;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: comprar a pesquisa (indice do enum Research). */
public record ResearchPayload(int research) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ResearchPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "research_buy"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ResearchPayload::research, ResearchPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
