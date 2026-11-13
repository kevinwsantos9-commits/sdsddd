package com.nivek.eclipse.creature;

import com.nivek.eclipse.data.EclipseData;
import com.nivek.eclipse.evolution.EvolutionManager;
import com.nivek.eclipse.event.MeteorEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

/**
 * Criaturas (Fase 09.1): "Experimentos" que cacam jogadores mutados.
 * Sao mobs vanilla (zumbi/aranha) com atributos reforcados, nome, brilho e uma tag.
 * Entidades proprias com modelo/IA customizada ficam para etapas futuras.
 */
public final class CreatureManager {
    public static final String TAG = "nivekeclipse_experiment";

    private static final int CHECK_INTERVAL = 600;   // 30 s
    private static final int CHANCE_ONE_IN = 3;
    private static final int MAX_NEARBY = 4;
    private static final int MIN_DIST = 20;
    private static final int MAX_DIST = 30;
    private static int timer = 0;

    private CreatureManager() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CreatureManager::tick);
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!entity.getTags().contains(TAG)) return;
            if (!(source.getEntity() instanceof ServerPlayer killer)) return;
            EvolutionManager.addMaterials(killer, 5 + killer.getRandom().nextInt(4));
            EvolutionManager.addXp(killer, 30);
            EclipseData.setExperimentKills(killer, EclipseData.getExperimentKills(killer) + 1);
        });
    }

    private static void tick(MinecraftServer server) {
        if (++timer < CHECK_INTERVAL) return;
        timer = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isSpectator() || EclipseData.getMutationLevel(player) < 1) continue;
            if (!(player.level() instanceof ServerLevel level)) continue;
            if (level.dimension() != Level.OVERWORLD || !MeteorEvent.isNightTime(level)) continue;
            if (level.getRandom().nextInt(CHANCE_ONE_IN) == 0) {
                spawnNear(player, level);
            }
        }
    }

    /** Cria um experimento perto do jogador (20-30 blocos). Retorna false se nao achou lugar ou ja ha muitos. */
    public static boolean spawnNear(ServerPlayer player, ServerLevel level) {
        AABB area = player.getBoundingBox().inflate(64.0);
        int nearby = level.getEntitiesOfClass(Mob.class, area, e -> e.getTags().contains(TAG)).size();
        if (nearby >= MAX_NEARBY) return false;

        RandomSource r = level.getRandom();
        for (int attempt = 0; attempt < 8; attempt++) {
            double angle = r.nextDouble() * Math.PI * 2;
            double dist = MIN_DIST + r.nextInt(MAX_DIST - MIN_DIST + 1);
            int x = (int) Math.floor(player.getX() + Math.cos(angle) * dist);
            int z = (int) Math.floor(player.getZ() + Math.sin(angle) * dist);
            if (!level.hasChunkAt(new BlockPos(x, 64, z))) continue;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos feet = new BlockPos(x, y, z);
            if (!level.getBlockState(feet).isAir() || !level.getFluidState(feet.below()).isEmpty()) continue;

            Mob mob = r.nextBoolean()
                    ? configure(new Zombie(level), "Experimento Falho", player)
                    : configure(new Spider(EntityType.SPIDER, level), "Experimento Rastejante", player);
            mob.setPos(x + 0.5, y, z + 0.5);
            level.addFreshEntity(mob);
            return true;
        }
        return false;
    }

    private static Mob configure(Mob mob, String name, ServerPlayer target) {
        int mutation = Math.max(1, EclipseData.getMutationLevel(target));
        double health = 20.0 + 10.0 * mutation;
        if (mob.getAttribute(Attributes.MAX_HEALTH) != null) {
            mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        }
        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0 + mutation);
        }
        mob.setHealth((float) health);
        mob.setCustomName(Component.literal(name));
        mob.setCustomNameVisible(true);
        mob.setGlowingTag(true);
        mob.setCanPickUpLoot(false);
        mob.addTag(TAG);
        mob.setTarget(target);
        return mob;
    }
}
