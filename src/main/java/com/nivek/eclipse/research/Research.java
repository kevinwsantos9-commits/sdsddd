package com.nivek.eclipse.research;

/** Pesquisas permanentes. O bit no Attachment RESEARCH e (1 << ordinal). */
public enum Research {
    REGENERATION("Tecido Regenerativo", "+4 de vida maxima", 2, 40),
    ENERGY_CORE("Nucleo de Energia", "Energia Eclipse regenera mais rapido", 2, 50),
    DENSE_FIBERS("Fibras Densas", "Resistencia a repulsao", 3, 70),
    PREDATOR("Instinto Predador", "+25% de dano de Lamina e Garras", 3, 90),
    METABOLISM("Metabolismo Evoluido", "+50% de XP Eclipse ao matar", 4, 120);

    public final String label;
    public final String description;
    public final int requiredMutation;
    /** Material gasto (nao devolvido). */
    public final int cost;

    Research(String label, String description, int requiredMutation, int cost) {
        this.label = label;
        this.description = description;
        this.requiredMutation = requiredMutation;
        this.cost = cost;
    }

    public int bit() {
        return 1 << ordinal();
    }
}
