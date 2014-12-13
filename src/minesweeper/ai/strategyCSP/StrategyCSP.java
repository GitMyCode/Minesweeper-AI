package minesweeper.ai.strategyCSP;

import minesweeper.ai.dataRepresentation.Graph;

public interface StrategyCSP {
    public void executeCSPonGraph(Graph graph);
    public String strategyToString();
    public String getExecutionLog();
    public void addLineToExecutionLog(String line);
}
