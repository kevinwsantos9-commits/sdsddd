package com.nivek.eclipse.research;

import com.nivek.eclipse.data.EclipseData;
import com.nivek.eclipse.implant.ImplantManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Pesquisas (Fase 08.3): compra validada no servidor (mutacao minima + material).
 * Efeitos por atributo sao aplicados aqui; os de energia, dano e XP sao lidos em
 * CombatManager e EvolutionManager via EclipseData.hasResearch.
 */
public final class ResearchManager {
    private static final int APPLY_INTERVAL = 20;
    private static int timer = 0;

    private ResearchManager() {}

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ResearchPayload.TYPE, ResearchPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ResearchPayload.TYPE,
                (payload, context) -> buy(context.player(), payload.research()));
        ServerTickEvents.END_SERVER_TICK.register(ResearchManager::tick);
    }

    private static void tick(MinecraftServer server) {
        if (++timer < APPLY_INTERVAL) return;
        timer = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            apply(player);
        }
    }

    private static void buy(ServerPlayer player, int index) {
        Research[] all = Research.values();
        if (index < 0 || index >= all.length) return;
        if (!player.isAlive() || player.isSpectator()) return;
        Research research = all[index];
        if (EclipseData.hasResearch(player, research)) {
            player.displayClientMessage(Component.literal("Pesquisa ja concluida."), true);
            return;
        }
        if (EclipseData.getMutationLevel(player) < research.requiredMutation) {
            player.displayClientMessage(Component.literal(
                    "Pesquisa bloqueada: requer mutacao nivel " + research.requiredMutation + "."), true);
            return;
        }
        int materials = EclipseData.getMaterials(player);
        if (materials < research.cost) {
            player.displayClientMessage(Component.literal(
                    "Material insuficiente: " + materials + " / " + research.cost + "."), true);
            return;
        }
        EclipseData.setMaterials(player, materials - research.cost);
        EclipseData.setResearch(player, EclipseData.getResearch(player) | research.bit());
        player.displayClientMessage(Component.literal("Pesquisa concluida: " + research.label + "."), false);
        apply(player);
    }

    /** Aplica/remove os modificadores de atributo das pesquisas (idempotente). */
    public static void apply(ServerPlayer player) {
        boolean active = EclipseData.getMutationLevel(player) >= 1;
        ImplantManager.setModifier(player, Attributes.MAX_HEALTH, "research_regeneration", 4.0,
                AttributeModifier.Operation.ADD_VALUE,
                active && EclipseData.hasResearch(player, Research.REGENERATION));
        ImplantManager.setModifier(player, Attributes.KNOCKBACK_RESISTANCE, "research_fibers", 0.3,
                AttributeModifier.Operation.ADD_VALUE,
                active && EclipseData.hasResearch(player, Research.DENSE_FIBERS));
    }
}
