package alonsod.mov.urjc.xorapp;

public class Level0 extends Level{
    public boolean SalidaBuena(boolean A, boolean B, boolean C, boolean D) {
        return !(A && B);
    }
    public boolean SalidaMala(boolean A, boolean B, boolean C, boolean D) {
        return !(C || D);
    }
}
