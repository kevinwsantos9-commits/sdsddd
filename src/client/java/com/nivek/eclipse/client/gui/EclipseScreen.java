package com.nivek.eclipse.client.gui;

import com.nivek.eclipse.data.EclipseData;
import com.nivek.eclipse.evolution.EvolutionManager;
import com.nivek.eclipse.implant.ImplantPayload;
import com.nivek.eclipse.implant.ImplantSlot;
import com.nivek.eclipse.research.Research;
import com.nivek.eclipse.research.ResearchPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Interface principal do ECLIPSE SYSTEM: navegacao lateral + conteudo da aba. */
public class EclipseScreen extends Screen {
    private static final int SIDEBAR_W = 104;
    private static final int HEADER_H = 22;
    private static final int MAX_LEVEL = 5;
    private static final String[] STAGES = {
            "Humano", "Fusao inicial", "Mutacao ativa", "Hibrido", "Avancado", "Eclipse"
    };

    private static final int COLOR_BG = 0xFF0B0F14;
    private static final int COLOR_SIDEBAR = 0xFF080B0F;
    private static final int COLOR_ACCENT = 0xFF00E5D0;
    private static final int COLOR_SELECTED = 0xFF12323A;
    private static final int COLOR_HOVER = 0xFF0F1D23;
    private static final int COLOR_TEXT = 0xFFE6F2F2;
    private static final int COLOR_DIM = 0xFF7F9A9A;

    private static final int IMPLANT_ROW_H = 24;

    private EclipseTab selected = EclipseTab.OVERVIEW;
    private int px, py, pw, ph, rowH;

    public EclipseScreen() {
        super(Component.literal("ECLIPSE SYSTEM"));
    }

    @Override
    protected void init() {
        pw = Math.min(this.width - 20, 420);
        ph = Math.min(this.height - 20, 240);
        px = (this.width - pw) / 2;
        py = (this.height - ph) / 2;
        rowH = Math.max(10, Math.min(16, (ph - HEADER_H - 12) / EclipseTab.values().length));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int tabTop(int index) {
        return py + HEADER_H + 6 + index * rowH;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xB0000000);

        // painel + borda
        g.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, COLOR_ACCENT);
        g.fill(px, py, px + pw, py + ph, COLOR_BG);

        // cabecalho
        g.drawString(this.font, "ECLIPSE SYSTEM", px + 8, py + 7, COLOR_ACCENT);
        g.fill(px, py + HEADER_H - 1, px + pw, py + HEADER_H, COLOR_ACCENT);

        // barra lateral
        g.fill(px, py + HEADER_H, px + SIDEBAR_W, py + ph, COLOR_SIDEBAR);
        EclipseTab[] tabs = EclipseTab.values();
        for (int i = 0; i < tabs.length; i++) {
            int top = tabTop(i);
            boolean hover = mouseX >= px && mouseX < px + SIDEBAR_W && mouseY >= top && mouseY < top + rowH;
            if (tabs[i] == selected) {
                g.fill(px, top, px + SIDEBAR_W, top + rowH, COLOR_SELECTED);
                g.fill(px, top, px + 2, top + rowH, COLOR_ACCENT);
            } else if (hover) {
                g.fill(px, top, px + SIDEBAR_W, top + rowH, COLOR_HOVER);
            }
            g.drawString(this.font, tabs[i].label, px + 8, top + (rowH - 8) / 2,
                    tabs[i] == selected ? COLOR_TEXT : COLOR_DIM);
        }

        // conteudo
        int cx = px + SIDEBAR_W + 12;
        int cy = py + HEADER_H + 10;
        g.drawString(this.font, selected.label, cx, cy, COLOR_ACCENT);
        if (selected == EclipseTab.OVERVIEW) {
            renderOverview(g, cx, cy + 16);
        } else if (selected == EclipseTab.EVOLUTION) {
            renderEvolution(g, cx, cy + 16);
        } else if (selected == EclipseTab.BESTIARY) {
            renderBestiary(g, cx, cy + 16);
        } else if (selected == EclipseTab.RESEARCH) {
            renderResearch(g, cx, mouseX, mouseY);
        } else if (selected == EclipseTab.IMPLANTS) {
            renderImplants(g, cx, mouseX, mouseY);
        } else {
            g.drawString(this.font, "Disponivel em breve (" + selected.note + ").", cx, cy + 16, COLOR_DIM);
        }
    }

    private void renderEvolution(GuiGraphics g, int x, int y) {
        Player player = this.minecraft != null ? this.minecraft.player : null;
        if (player == null) return;
        int level = Math.min(EclipseData.getMutationLevel(player), MAX_LEVEL);
        int barW = Math.max(40, pw - SIDEBAR_W - 36);

        g.drawString(this.font, "Estagio atual: " + STAGES[level], x, y, COLOR_TEXT);
        int need = EvolutionManager.xpToNext(level);
        if (need > 0) {
            int xp = Math.min(EclipseData.getXp(player), need);
            g.drawString(this.font, "XP Eclipse: " + xp + " / " + need, x, y + 14, COLOR_TEXT);
            g.fill(x, y + 26, x + barW, y + 34, 0xFF1A2429);
            g.fill(x, y + 26, x + barW * xp / need, y + 34, COLOR_ACCENT);
        } else {
            g.drawString(this.font, level >= MAX_LEVEL ? "Evolucao maxima alcancada." : "Sem mutacao.", x, y + 14, COLOR_DIM);
        }
        for (int i = 1; i <= MAX_LEVEL; i++) {
            int rowY = y + 44 + (i - 1) * 12;
            boolean reached = level >= i;
            g.fill(x, rowY + 1, x + 6, rowY + 7, reached ? COLOR_ACCENT : 0xFF1A2429);
            g.drawString(this.font, i + "  " + STAGES[i], x + 12, rowY, reached ? COLOR_TEXT : COLOR_DIM);
        }
    }

    private void renderBestiary(GuiGraphics g, int x, int y) {
        Player player = this.minecraft != null ? this.minecraft.player : null;
        if (player == null) return;
        g.drawString(this.font, "Experimentos derrotados: " + EclipseData.getExperimentKills(player), x, y, COLOR_TEXT);
        g.drawString(this.font, "Experimento Falho", x, y + 20, COLOR_ACCENT);
        g.drawString(this.font, "Zumbi alterado. Surge a noite e cai-a em voce.", x, y + 32, COLOR_DIM);
        g.drawString(this.font, "Experimento Rastejante", x, y + 50, COLOR_ACCENT);
        g.drawString(this.font, "Aranha alterada. Mais rapida, mesma fome.", x, y + 62, COLOR_DIM);
        g.drawString(this.font, "Cada abate rende material e XP extra.", x, y + 80, COLOR_DIM);
    }

    private void renderResearch(GuiGraphics g, int x, int mouseX, int mouseY) {
        Player player = this.minecraft != null ? this.minecraft.player : null;
        if (player == null) return;
        int mutation = EclipseData.getMutationLevel(player);
        int w = pw - SIDEBAR_W - 24;
        g.drawString(this.font, "Material: " + EclipseData.getMaterials(player), x + 90, implantsTop() - 16, COLOR_TEXT);
        Research[] all = Research.values();
        for (int i = 0; i < all.length; i++) {
            Research r = all[i];
            int top = implantsTop() + i * IMPLANT_ROW_H;
            boolean done = EclipseData.hasResearch(player, r);
            boolean locked = mutation < r.requiredMutation;
            boolean hover = mouseX >= x && mouseX < x + w && mouseY >= top && mouseY < top + IMPLANT_ROW_H - 2;
            g.fill(x, top, x + w, top + IMPLANT_ROW_H - 2, hover ? COLOR_SELECTED : COLOR_HOVER);
            if (done) g.fill(x, top, x + 2, top + IMPLANT_ROW_H - 2, COLOR_ACCENT);
            String state = done ? "CONCLUIDA"
                    : locked ? "BLOQUEADA (mutacao " + r.requiredMutation + ")"
                    : "custo " + r.cost;
            g.drawString(this.font, r.label + "  -  " + state, x + 6, top + 3, locked && !done ? COLOR_DIM : COLOR_TEXT);
            g.drawString(this.font, r.description, x + 6, top + 13, COLOR_DIM);
        }
    }

    private int implantsTop() {
        return py + HEADER_H + 10 + 16;
    }

    private void renderImplants(GuiGraphics g, int x, int mouseX, int mouseY) {
        Player player = this.minecraft != null ? this.minecraft.player : null;
        if (player == null) return;
        int mask = EclipseData.getImplants(player);
        int mutation = EclipseData.getMutationLevel(player);
        int w = pw - SIDEBAR_W - 24;
        g.drawString(this.font, "Material: " + EclipseData.getMaterials(player), x + 90, implantsTop() - 16, COLOR_TEXT);
        ImplantSlot[] slots = ImplantSlot.values();
        for (int i = 0; i < slots.length; i++) {
            ImplantSlot slot = slots[i];
            int top = implantsTop() + i * IMPLANT_ROW_H;
            boolean locked = mutation < slot.requiredMutation;
            boolean installed = (mask & slot.bit()) != 0;
            boolean hover = mouseX >= x && mouseX < x + w && mouseY >= top && mouseY < top + IMPLANT_ROW_H - 2;
            g.fill(x, top, x + w, top + IMPLANT_ROW_H - 2, hover ? COLOR_SELECTED : COLOR_HOVER);
            if (installed) g.fill(x, top, x + 2, top + IMPLANT_ROW_H - 2, COLOR_ACCENT);
            String state = locked ? "BLOQUEADO (mutacao " + slot.requiredMutation + ")"
                    : installed ? "INSTALADO" : "VAZIO (custo " + slot.cost + ")";
            g.drawString(this.font, slot.label + "  -  " + state, x + 6, top + 3,
                    locked ? COLOR_DIM : COLOR_TEXT);
            g.drawString(this.font, slot.description, x + 6, top + 13, COLOR_DIM);
        }
    }

    private void renderOverview(GuiGraphics g, int x, int y) {
        Player player = this.minecraft != null ? this.minecraft.player : null;
        if (player == null) return;

        int level = Math.min(EclipseData.getMutationLevel(player), MAX_LEVEL);
        g.drawString(this.font, "Jogador: " + player.getName().getString(), x, y, COLOR_TEXT);
        g.drawString(this.font, "Estagio: " + STAGES[level], x, y + 14, COLOR_TEXT);
        g.drawString(this.font, "Mutacao: " + level + " / " + MAX_LEVEL, x, y + 28, COLOR_TEXT);

        int barW = Math.max(40, pw - SIDEBAR_W - 36);
        int barY = y + 44;
        g.fill(x, barY, x + barW, barY + 8, 0xFF1A2429);
        g.fill(x, barY, x + barW * level / MAX_LEVEL, barY + 8, COLOR_ACCENT);

        int energy = EclipseData.getEnergy(player);
        g.drawString(this.font, "Energia: " + energy + " / " + EclipseData.MAX_ENERGY, x, y + 58, COLOR_TEXT);
        int energyY = y + 72;
        g.drawString(this.font, "Material: " + EclipseData.getMaterials(player), x, y + 86, COLOR_TEXT);
        g.fill(x, energyY, x + barW, energyY + 8, 0xFF1A2429);
        g.fill(x, energyY, x + barW * energy / EclipseData.MAX_ENERGY, energyY + 8, 0xFF9B4DFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && selected == EclipseTab.RESEARCH) {
            int cx = px + SIDEBAR_W + 12;
            int w = pw - SIDEBAR_W - 24;
            int relY = (int) mouseY - implantsTop();
            int index = relY / IMPLANT_ROW_H;
            if (mouseX >= cx && mouseX < cx + w && relY >= 0 && index < Research.values().length) {
                ClientPlayNetworking.send(new ResearchPayload(index));
                return true;
            }
        }
        if (button == 0 && selected == EclipseTab.IMPLANTS) {
            int cx = px + SIDEBAR_W + 12;
            int w = pw - SIDEBAR_W - 24;
            int relY = (int) mouseY - implantsTop();
            int index = relY / IMPLANT_ROW_H;
            if (mouseX >= cx && mouseX < cx + w && relY >= 0 && index < ImplantSlot.values().length) {
                ClientPlayNetworking.send(new ImplantPayload(index));
                return true;
            }
        }
        if (button == 0 && mouseX >= px && mouseX < px + SIDEBAR_W) {
            EclipseTab[] tabs = EclipseTab.values();
            for (int i = 0; i < tabs.length; i++) {
                int top = tabTop(i);
                if (mouseY >= top && mouseY < top + rowH) {
                    selected = tabs[i];
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
