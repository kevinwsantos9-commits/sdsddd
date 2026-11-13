package com.nivek.eclipse.implant;

/** Slots de implante. O bit do slot no Attachment IMPLANTS e (1 << ordinal). */
public enum ImplantSlot {
    HEAD("Cabeca", "Sensor ocular: visao noturna", 1, 10),
    ARMS("Bracos", "+2 de dano de ataque", 1, 15),
    BACK("Costas", "+3 blocos de queda segura", 2, 20),
    LEGS("Pernas", "+10% de velocidade e pulo maior", 2, 25),
    CHEST("Peitoral", "+4 de armadura", 3, 30);

    public final String label;
    public final String description;
    /** Nivel de mutacao minimo para instalar. */
    public final int requiredMutation;
    /** Material gasto ao instalar (devolvido ao remover). */
    public final int cost;

    ImplantSlot(String label, String description, int requiredMutation, int cost) {
        this.label = label;
        this.description = description;
        this.requiredMutation = requiredMutation;
        this.cost = cost;
    }

    public int bit() {
        return 1 << ordinal();
    }
}
