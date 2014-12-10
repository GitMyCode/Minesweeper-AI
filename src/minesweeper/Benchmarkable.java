package minesweeper;

public interface Benchmarkable {

    public int getNbProbabilitySuccess();
    public int getNbProbabilityFails();
    public double getTrivialMoveRate();
    public double getCSPMoveRate();
    public double getUncertainMoveRate();
}
