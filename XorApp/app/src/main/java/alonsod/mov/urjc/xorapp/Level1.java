package alonsod.mov.urjc.xorapp;

public class Level1 extends Level{
    public String getLevelName() { return "nivel 1";}
    public boolean SalidaBuena(boolean A, boolean B, boolean C, boolean D) {
        return (A && B) || (C || D);
    }
    public boolean SalidaMala(boolean A, boolean B, boolean C, boolean D) {
        return !((A && B) && (C || D));
    }
}
