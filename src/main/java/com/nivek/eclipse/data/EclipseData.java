package com.nivek.eclipse.data;

import com.nivek.eclipse.research.Research;
import net.minecraft.world.entity.player.Player;

/** Acesso simples aos dados do jogador. */
public final class EclipseData {
    public static final int MAX_ENERGY = 100;

    private EclipseData() {}

    public static int getEnergy(Player player) {
        return player.getAttachedOrElse(ModAttachments.ENERGY, MAX_ENERGY);
    }

    public static void setEnergy(Player player, int energy) {
        player.setAttached(ModAttachments.ENERGY, Math.max(0, Math.min(MAX_ENERGY, energy)));
    }

    public static int getExperimentKills(Player player) {
        return player.getAttachedOrElse(ModAttachments.EXPERIMENT_KILLS, 0);
    }

    public static void setExperimentKills(Player player, int kills) {
        player.setAttached(ModAttachments.EXPERIMENT_KILLS, Math.max(0, kills));
    }

    public static int getResearch(Player player) {
        return player.getAttachedOrElse(ModAttachments.RESEARCH, 0);
    }

    public static void setResearch(Player player, int mask) {
        player.setAttached(ModAttachments.RESEARCH, mask);
    }

    public static boolean hasResearch(Player player, Research research) {
        return (getResearch(player) & research.bit()) != 0;
    }

    public static int getMaterials(Player player) {
        return player.getAttachedOrElse(ModAttachments.MATERIALS, 0);
    }

    public static void setMaterials(Player player, int amount) {
        player.setAttached(ModAttachments.MATERIALS, Math.max(0, amount));
    }

    public static int getXp(Player player) {
        return player.getAttachedOrElse(ModAttachments.EVOLUTION_XP, 0);
    }

    public static void setXp(Player player, int xp) {
        player.setAttached(ModAttachments.EVOLUTION_XP, Math.max(0, xp));
    }

    public static int getImplants(Player player) {
        return player.getAttachedOrElse(ModAttachments.IMPLANTS, 0);
    }

    public static void setImplants(Player player, int mask) {
        player.setAttached(ModAttachments.IMPLANTS, mask);
    }

    public static int getMutationLevel(Player player) {
        return player.getAttachedOrElse(ModAttachments.MUTATION_LEVEL, 0);
    }

    public static void setMutationLevel(Player player, int level) {
        player.setAttached(ModAttachments.MUTATION_LEVEL, Math.max(0, level));
    }
}
