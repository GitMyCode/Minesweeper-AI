package minesweeper.ai;

import minesweeper.*;
import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.ai.utilCSP.Graph;
import minesweeper.ai.utilCSP.TimeOver;

import static minesweeper.Case.*;

import java.util.*;

/**
 * Created by martin on 18/11/14.
 */
public class CSPGraph implements ArtificialPlayer {

    private long timer;
    private long remain;
    private boolean END = false;
    private final int LIMITE = 10;
    private Grid gameGrid;

    //Set<Integer> undiscoveredFrontier;
    private List<Integer> nbMatchByFrontier;
    private Integer nbPossibilite = 0;
    Graph graph;

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {

        gameGrid = grid;
        Case[] gridCopy = grid.getCpyPlayerView();
        startTimer(delay);
        Set<Move> movesToPlay;
        nbPossibilite = 0;
        movesToPlay = grid.checkForSafeMoves();

        if(!movesToPlay.isEmpty()) { return movesToPlay; }

        // Contient le nombre de possibilités pour cette frontière
        nbMatchByFrontier = new ArrayList<Integer>();
        computeMoves(grid);
        addMoves(grid, gridCopy, movesToPlay);

        return movesToPlay;
    }

    public void computeMoves(Grid g) {
        try {
            executeMoveComputation(g);
        } catch (TimeOver ignored) {
            System.out.println("timeout");
        }
    }

    private void executeMoveComputation(Grid g) throws TimeOver {

        Case[] grid = g.getCpyPlayerView();
        graph = new Graph(g);
        System.out.println("va pour le csp");
        CSPonAllFrontiers();
    }

    private void CSPonAllFrontiers() throws TimeOver {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            List<Graph.HintNode> hintBorder = graph.allHintNode.get(i);
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbPossibilite = 0;
            recurseCSP(hintBorder, fringeNodes, 0);
            nbMatchByFrontier.add(nbPossibilite);
        }
    }

    /*
     Etapes:
        1) check si les contraintes des variables ne sont pas violés ( exemple un indice de 2 est entourer de 3 flag)
            Si la cette configuration ne marche pas on backtrack
        2) check si on est au bout de la liste de variable. Si oui alors les flag placé sont compater et chaque case 
            de la frontiere incrément leur compter de flag si elle sont flaggé. (Pour calculer les probs)
        3) calcul de toutes les combinaison possible de placement de drapeau autour de la variable présente.
        4) pour chacune des combinaison trouvé. Les drapeau sont placées et on récurse.
    **/
    boolean recurseCSP(List<Graph.HintNode> hintNodes, List<Graph.FringeNode> fringeNodes, int index) throws TimeOver {

        if (isTimeUp()) { throw new TimeOver(); }
        if (!allFlagsOkay(hintNodes, index)) { return false; }

        // Solution trouvée
        if (index >= hintNodes.size()) {
            computeFlagHits(fringeNodes);
            nbPossibilite++;
            return true;
        }

        Graph.HintNode variableToSatisfy = hintNodes.get(index);
        variableToSatisfy.updateSurroundingAwareness();

        if (variableToSatisfy.isOverAssigned()) { return false; }
        if (variableToSatisfy.isSatisfied()) {
            return recurseCSP(hintNodes, fringeNodes, index + 1);
        }

        List<Graph.FringeNode> undiscoveredFringe = variableToSatisfy.getUndiscoveredFringe();
        ArrayList<int[]> allFlagCombinations = variableToSatisfy.getAllFlagCombinations();

        for (int[] combination : allFlagCombinations) {
            // Nécessaire pour la récursion
            int nbFlagToPlaceHere = variableToSatisfy.nbFlagToPlace;

            addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            recurseCSP(hintNodes, fringeNodes, index + 1);
            removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
        }

        return false;
    }

    private boolean allFlagsOkay(List<Graph.HintNode> hintNodes, int nbDone) {
        for (int i = 0; i < nbDone; i++) {
            Graph.HintNode hintNode = hintNodes.get(i);
            int value = hintNode.value;

            Set<Graph.FringeNode> neighborsFringe = hintNode.connectedFringe;

            int nbFlag = 0;
            for (Graph.FringeNode fn : neighborsFringe) {
                if (fn.state == FLAGED) { nbFlag++; }
            }

            if (nbFlag != value) {
                return false;
            }
        }
        return true;
    }

    private void computeFlagHits(List<Graph.FringeNode> fringeNodes) {
        for (Graph.FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) { fn.nbFlagHits++; }
        }
    }

    private void addFlagsToUndiscoveredFringe(List<Graph.FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
    }

    private void removeFlagsFromUndiscoveredFringe(List<Graph.FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
            fringeToFlag.state = UNDISCOVERED;
        }
    }

    private void addMoves(Grid grid, Case[] gridCopy, Set<Move> movesToPlay) {
        if (movesToPlay.isEmpty()) {
            addSafeMovesAndFlags(movesToPlay);
            if (movesToPlay.isEmpty()) {
                addRandomMove(grid, gridCopy, movesToPlay);
            }
        }
    }

    private void addSafeMovesAndFlags(Set<Move> movesToPlay) {
        System.out.println("essai avec les resultats csp");
        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
            // Cases non-découvertes qui côtoient une case découverte
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int nbPossibilityHere = nbMatchByFrontier.get(frontierIndex);

            for (Graph.FringeNode fn : fringeNodes) {
                if (fn.nbFlagHits == 0) {
                    // 0% Mine
                    movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                } else if (fn.nbFlagHits == nbPossibilityHere) {
                    // 100% Mine
                    movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                }
            }
        }
    }

    private void addRandomMove(Grid grid, Case[] gridCopy, Set<Move> movesToPlay) {
        List<Integer> legalMoves = new ArrayList<Integer>();
        for (int i = 0; i < grid.length; i++) {
            if (gridCopy[i] == UNDISCOVERED) {
                legalMoves.add(i);
            }
        }

        Random ran = new Random();
        int index = legalMoves.get(ran.nextInt(legalMoves.size()));
        movesToPlay.add(new Move(index, Coup.SHOW));
    }

    private void startTimer(int delai) {
        END = false;
        timer = System.currentTimeMillis();
        remain = delai;
    }

    private String showTimeRemaing() { return ("Time: " + timeRemaining() + " ms"); }

    public long timeRemaining() {
        long elaspsed = (System.currentTimeMillis() - timer);
        return remain - elaspsed;
    }

    public boolean isTimeUp() {
        END = (timeRemaining() < LIMITE) ? true: END;
        return END;
    }

    @Override
    public String getName() { return "CSP-Martin"; }
}
