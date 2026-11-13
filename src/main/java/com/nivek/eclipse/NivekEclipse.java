package com.nivek.eclipse;

import com.nivek.eclipse.combat.CombatManager;
import com.nivek.eclipse.command.EclipseCommand;
import com.nivek.eclipse.creature.CreatureManager;
import com.nivek.eclipse.data.ModAttachments;
import com.nivek.eclipse.event.FusionEvent;
import com.nivek.eclipse.event.MeteorEvent;
import com.nivek.eclipse.evolution.EvolutionManager;
import com.nivek.eclipse.implant.ImplantManager;
import com.nivek.eclipse.registry.ModBlocks;
import com.nivek.eclipse.research.ResearchManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NivekEclipse implements ModInitializer {
    public static final String MOD_ID = "nivekeclipse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModAttachments.init();
        ModBlocks.init();
        MeteorEvent.register();
        FusionEvent.register();
        CombatManager.register();
        ImplantManager.register();
        EvolutionManager.register();
        ResearchManager.register();
        CreatureManager.register();
        EclipseCommand.register();
        LOGGER.info("NIVEK: ECLIPSE inicializado (Fase 09.1 - Experimentos)");
    }
}
