package com.nivek.eclipse.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.nivek.eclipse.creature.CreatureManager;
import com.nivek.eclipse.data.EclipseData;
import com.nivek.eclipse.data.ModAttachments;
import com.nivek.eclipse.event.MeteorEvent;
import com.nivek.eclipse.evolution.EvolutionManager;
import com.nivek.eclipse.implant.ImplantManager;
import com.nivek.eclipse.research.ResearchManager;
import com.nivek.eclipse.implant.ImplantSlot;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** Comandos de teste (op nivel 2): /eclipse meteor | status | mutation <0-5> | xp <n> | material <n> | experiment | implants all|none | reset. */
public final class EclipseCommand {
    private EclipseCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("eclipse")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("meteor").executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            MeteorEvent.start(player, (ServerLevel) player.level());
                            ctx.getSource().sendSuccess(() -> Component.literal("Meteoro iniciado."), false);
                            return 1;
                        }))
                        .then(Commands.literal("status").executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            int mutation = EclipseData.getMutationLevel(player);
                            boolean meteor = player.getAttachedOrElse(ModAttachments.METEOR_EVENT_DONE, false);
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Mutacao: " + mutation + " | Meteoro ja ocorreu: " + meteor), false);
                            return 1;
                        }))
                        .then(Commands.literal("mutation")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 5)).executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int level = IntegerArgumentType.getInteger(ctx, "level");
                                    EclipseData.setMutationLevel(player, level);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Mutacao definida: " + level), false);
                                    return 1;
                                })))
                        .then(Commands.literal("xp")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100000)).executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    EvolutionManager.addXp(player, amount);
                                    ctx.getSource().sendSuccess(() -> Component.literal("XP Eclipse adicionado: " + amount), false);
                                    return 1;
                                })))
                        .then(Commands.literal("experiment").executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            boolean ok = CreatureManager.spawnNear(player, (ServerLevel) player.level());
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    ok ? "Experimento criado." : "Nao foi possivel criar (muitos por perto ou sem espaco)."), false);
                            return 1;
                        }))
                        .then(Commands.literal("material")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100000)).executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    EvolutionManager.addMaterials(player, amount);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Material adicionado: " + amount), false);
                                    return 1;
                                })))
                        .then(Commands.literal("implants")
                                .then(Commands.literal("all").executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    EclipseData.setImplants(player, (1 << ImplantSlot.values().length) - 1);
                                    ImplantManager.apply(player);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Todos os implantes instalados (teste)."), false);
                                    return 1;
                                }))
                                .then(Commands.literal("none").executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    EclipseData.setImplants(player, 0);
                                    ImplantManager.apply(player);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Implantes removidos."), false);
                                    return 1;
                                })))
                        .then(Commands.literal("reset").executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            player.setAttached(ModAttachments.METEOR_EVENT_DONE, false);
                            EclipseData.setMutationLevel(player, 0);
                            EclipseData.setXp(player, 0);
                            EclipseData.setMaterials(player, 0);
                            EclipseData.setExperimentKills(player, 0);
                            EclipseData.setResearch(player, 0);
                            ResearchManager.apply(player);
                            EclipseData.setImplants(player, 0);
                            ImplantManager.apply(player);
                            ctx.getSource().sendSuccess(() -> Component.literal("Meteoro e mutacao resetados."), false);
                            return 1;
                        }))));
    }
}
