package minesweeper.ai.utilCSP.strategyCSP;

import minesweeper.ai.utilCSP.Graph;

/**
 * Created by MB on 11/29/2014.
 */
public interface StrategyCSP {

    public void executeCSPonGraph(Graph graph);
    public String strategyToString();

}
