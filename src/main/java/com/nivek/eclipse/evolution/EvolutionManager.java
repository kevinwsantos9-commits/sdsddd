package com.nivek.eclipse.evolution;

import com.nivek.eclipse.data.EclipseData;
import com.nivek.eclipse.research.Research;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

/**
 * Evolucao (Fase 08.1): experiencia Eclipse e os cinco estagios.
 * O estagio e o proprio nivel de mutacao (1..5). XP guardado = progresso rumo ao proximo estagio.
 */
public final class EvolutionManager {
    public static final int MAX_LEVEL = 5;
    /** XP necessario para sair do nivel (indice) rumo ao proximo. Indice 0 e 5 nao usados. */
    private static final int[] XP_TO_NEXT = {0, 100, 300, 700, 1500, 0};

    private EvolutionManager() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(source.getEntity() instanceof ServerPlayer killer)) return;
            if (entity instanceof Player) return;
            int amount = xpFor(entity);
            if (amount > 0 && EclipseData.hasResearch(killer, Research.METABOLISM)) amount = amount * 3 / 2;
            if (amount > 0) addXp(killer, amount);
            addMaterials(killer, entity);
        });
    }

    /** Material dropado ao matar: hostis dao 1-3, outros 0-1. Exige mutacao >= 1. */
    private static void addMaterials(ServerPlayer player, LivingEntity victim) {
        if (EclipseData.getMutationLevel(player) < 1) return;
        int gain = victim instanceof Enemy ? 1 + player.getRandom().nextInt(3) : player.getRandom().nextInt(2);
        if (gain > 0) addMaterials(player, gain);
    }

    public static void addMaterials(ServerPlayer player, int amount) {
        if (amount <= 0) return;
        EclipseData.setMaterials(player, EclipseData.getMaterials(player) + amount);
    }

    /** XP necessario para evoluir a partir do nivel dado; 0 se o nivel nao evolui (0 ou maximo). */
    public static int xpToNext(int level) {
        return level >= 1 && level < MAX_LEVEL ? XP_TO_NEXT[level] : 0;
    }

    private static int xpFor(LivingEntity victim) {
        int base = Math.max(1, (int) victim.getMaxHealth());
        return victim instanceof Enemy ? base * 2 : base;
    }

    /** Soma XP; so evolui quem ja tem a mutacao nivel 1 e ainda nao esta no maximo. */
    public static void addXp(ServerPlayer player, int amount) {
        if (amount <= 0) return;
        int level = EclipseData.getMutationLevel(player);
        if (level < 1 || level >= MAX_LEVEL) return;
        int xp = EclipseData.getXp(player) + amount;
        boolean leveled = false;
        while (level < MAX_LEVEL && xp >= xpToNext(level)) {
            xp -= xpToNext(level);
            level++;
            leveled = true;
        }
        if (level >= MAX_LEVEL) xp = 0;
        EclipseData.setXp(player, xp);
        if (leveled) {
            EclipseData.setMutationLevel(player, level);
            onLevelUp(player, level);
        }
    }

    private static void onLevelUp(ServerPlayer player, int level) {
        player.displayClientMessage(Component.literal("EVOLUCAO! Mutacao nivel " + level + " alcancada."), false);
        if (player.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(),
                    60, 0.5, 0.9, 0.5, 0.15);
            sl.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.5F, 0.7F);
        }
    }
}
