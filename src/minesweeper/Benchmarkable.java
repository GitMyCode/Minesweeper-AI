package minesweeper;

public interface Benchmarkable {

    public boolean isProbabilistic();

    public double getProbabilitySuccessRate();
    public double getTrivialMoveRate();
    public double getCSPMoveRate();
    public double getProbabilisticMoveRate();
}
