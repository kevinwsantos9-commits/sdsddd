package com.nivek.eclipse.data;

import com.mojang.serialization.Codec;
import com.nivek.eclipse.NivekEclipse;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

/** Dados salvos por jogador. */
public final class ModAttachments {
    /** true depois que o evento do meteoro ja foi disparado para o jogador. */
    public static final AttachmentType<Boolean> METEOR_EVENT_DONE = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "meteor_event_done"),
            builder -> builder.initializer(() -> false).persistent(Codec.BOOL).copyOnDeath());

    /** Nivel de mutacao do jogador (0 = nenhuma). Sincronizado com todos os clientes (render do braco). */
    public static final AttachmentType<Integer> MUTATION_LEVEL = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "mutation_level"),
            builder -> builder.initializer(() -> 0).persistent(Codec.INT).copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.all()));

    /** Energia Eclipse (0-100). Sincronizada so com o proprio jogador. */
    public static final AttachmentType<Integer> ENERGY = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "energy"),
            builder -> builder.initializer(() -> 100).persistent(Codec.INT)
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly()));

    /** Mascara de bits dos implantes instalados (ver ImplantSlot). Sincronizada com todos (visual na 7.2). */
    public static final AttachmentType<Integer> IMPLANTS = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "implants"),
            builder -> builder.initializer(() -> 0).persistent(Codec.INT).copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.all()));

    /** Progresso de XP Eclipse rumo ao proximo estagio. Sincronizado so com o proprio jogador. */
    public static final AttachmentType<Integer> EVOLUTION_XP = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "evolution_xp"),
            builder -> builder.initializer(() -> 0).persistent(Codec.INT).copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly()));

    /** Material Eclipse (moeda dos implantes). Sincronizado so com o proprio jogador. */
    public static final AttachmentType<Integer> MATERIALS = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "materials"),
            builder -> builder.initializer(() -> 0).persistent(Codec.INT).copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly()));

    /** Mascara de bits das pesquisas concluidas (ver Research). Sincronizada so com o proprio jogador. */
    public static final AttachmentType<Integer> RESEARCH = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "research"),
            builder -> builder.initializer(() -> 0).persistent(Codec.INT).copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly()));

    /** Quantos experimentos o jogador ja derrotou (Bestiario). Sincronizado so com o proprio jogador. */
    public static final AttachmentType<Integer> EXPERIMENT_KILLS = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "experiment_kills"),
            builder -> builder.initializer(() -> 0).persistent(Codec.INT).copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly()));

    private ModAttachments() {}

    public static void init() {}
}
