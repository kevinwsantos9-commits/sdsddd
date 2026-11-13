package com.nivek.eclipse.implant;

import com.nivek.eclipse.NivekEclipse;
import com.nivek.eclipse.data.EclipseData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Implantes (Fase 07.1): instalacao validada no servidor e efeitos funcionais.
 * Efeitos = modificadores de atributo permanentes (idempotentes, reaplicados a cada 1 s)
 * e visao noturna para a cabeca. Visual 3D dos implantes: etapa 7.2.
 */
public final class ImplantManager {
    private static final int APPLY_INTERVAL = 20;
    private static int timer = 0;

    private ImplantManager() {}

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ImplantPayload.TYPE, ImplantPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ImplantPayload.TYPE,
                (payload, context) -> toggle(context.player(), payload.slot()));
        ServerTickEvents.END_SERVER_TICK.register(ImplantManager::tick);
    }

    private static void tick(MinecraftServer server) {
        if (++timer < APPLY_INTERVAL) return;
        timer = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            apply(player);
        }
    }

    private static void toggle(ServerPlayer player, int slotIndex) {
        ImplantSlot[] slots = ImplantSlot.values();
        if (slotIndex < 0 || slotIndex >= slots.length) return;
        if (!player.isAlive() || player.isSpectator()) return;
        ImplantSlot slot = slots[slotIndex];
        int mutation = EclipseData.getMutationLevel(player);
        if (mutation < slot.requiredMutation) {
            player.displayClientMessage(Component.literal(
                    "Implante bloqueado: requer mutacao nivel " + slot.requiredMutation + "."), true);
            return;
        }
        int current = EclipseData.getImplants(player);
        boolean installing = (current & slot.bit()) == 0;
        int materials = EclipseData.getMaterials(player);
        if (installing) {
            if (materials < slot.cost) {
                player.displayClientMessage(Component.literal(
                        "Material insuficiente: " + materials + " / " + slot.cost + "."), true);
                return;
            }
            EclipseData.setMaterials(player, materials - slot.cost);
        } else {
            EclipseData.setMaterials(player, materials + slot.cost);
        }
        int mask = current ^ slot.bit();
        EclipseData.setImplants(player, mask);
        boolean installed = installing;
        player.displayClientMessage(Component.literal(
                "Implante " + slot.label + (installed ? " instalado." : " removido.")), true);
        apply(player);
    }

    /** Aplica/remove os efeitos conforme os implantes instalados (so valem com mutacao >= 1). */
    public static void apply(ServerPlayer player) {
        boolean active = EclipseData.getMutationLevel(player) >= 1;
        int mask = EclipseData.getImplants(player);
        for (ImplantSlot slot : ImplantSlot.values()) {
            boolean on = active && (mask & slot.bit()) != 0;
            switch (slot) {
                case HEAD -> {
                    if (on) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false, false));
                    }
                }
                case ARMS -> setModifier(player, Attributes.ATTACK_DAMAGE, "implant_arms", 2.0,
                        AttributeModifier.Operation.ADD_VALUE, on);
                case BACK -> setModifier(player, Attributes.SAFE_FALL_DISTANCE, "implant_back", 3.0,
                        AttributeModifier.Operation.ADD_VALUE, on);
                case LEGS -> {
                    setModifier(player, Attributes.MOVEMENT_SPEED, "implant_legs_speed", 0.10,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE, on);
                    setModifier(player, Attributes.JUMP_STRENGTH, "implant_legs_jump", 0.10,
                            AttributeModifier.Operation.ADD_VALUE, on);
                }
                case CHEST -> setModifier(player, Attributes.ARMOR, "implant_chest", 4.0,
                        AttributeModifier.Operation.ADD_VALUE, on);
            }
        }
    }

    public static void setModifier(ServerPlayer player, Holder<Attribute> attribute, String name,
                                    double amount, AttributeModifier.Operation op, boolean on) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, name);
        if (on) {
            if (!instance.hasModifier(id)) {
                instance.addPermanentModifier(new AttributeModifier(id, amount, op));
            }
        } else if (instance.hasModifier(id)) {
            instance.removeModifier(id);
        }
    }
}
