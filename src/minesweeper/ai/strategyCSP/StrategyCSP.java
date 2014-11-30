package minesweeper.ai.strategyCSP;

import minesweeper.ai.dataRepresentation.Graph;

/**
 * Created by MB on 11/29/2014.
 */
public interface StrategyCSP {


    public void executeCSPonGraph (Graph graph);

    public String strategyToString ();

    public String getExecutionLog ();

    public void addLineToExecutionLog (String line);

}
