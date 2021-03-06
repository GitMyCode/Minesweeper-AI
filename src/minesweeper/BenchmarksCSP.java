package minesweeper;

import minesweeper.ai.dataRepresentation.Graph;
import minesweeper.ai.strategyCSP.ForwardCheckCSP;
import minesweeper.ai.strategyCSP.SimpleCSP;
import minesweeper.ai.strategyCSP.StrategyCSP;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MB on 11/30/2014.
 */
public class BenchmarksCSP {

    public static final StrategyCSP[] CSPS = {
            new SimpleCSP(),
            new ForwardCheckCSP()

    };

    public static void main(String[] args) {

        File directory = new File("src/minesweeper/ai/utilCSP/benchmarkGridCSP");

        File[] allTestFile = directory.listFiles();
        final int columnSize = 40;

        List<String[]> logsList = new ArrayList<String[]>();
        for (StrategyCSP csp : CSPS) {
            System.out.println(csp.strategyToString());
            csp.addLineToExecutionLog(csp.strategyToString());
            for (File testFile : allTestFile) {
                Grid testGrid = new Grid();
                testGrid.loadFromFile(testFile);
                Graph testGraph = new Graph(testGrid);
                csp.addLineToExecutionLog("--------- " + testFile.getName() + " ---------");
                csp.executeCSPonGraph(testGraph);

            }
            logsList.add(csp.getExecutionLog().split("\n"));
        }

        for (int logLine = 0; logLine < logsList.get(0).length; logLine++) {
            String logStringLine = "";
            for (int cspLog = 0; cspLog < logsList.size(); cspLog++) {
                String logLineForThisCSP = logsList.get(cspLog)[logLine];
                String end = "";
                if (cspLog == logsList.size() - 1) {
                    end = "\n";
                } else {
                    int spaceSize = columnSize - logLineForThisCSP.length();
                    char[] spaceArray = new char[spaceSize];
                    Arrays.fill(spaceArray, ' ');
                    end = new String(spaceArray);
                }


                logStringLine += logLineForThisCSP + end;
            }
            System.out.print(logStringLine);
        }

    }


}
