package minesweeper.ai.dataRepresentation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static minesweeper.Case.FLAGED;
import static minesweeper.Case.UNDISCOVERED;

/**
 * Created by MB on 11/29/2014.
 */
public class HintNode extends Node {

    public Set<FringeNode> connectedFringe;
    public Set<HintNode> connectedHint;
    public int value;
    public int nbFlagToPlace;
    public int nbUndiscoveredNeighbors;

    public HintNode (int index, int value, int nbUndiscoveredNeighbors) {
        super(index);
        this.value = value;
        nbFlagToPlace = value;
        this.nbUndiscoveredNeighbors = nbUndiscoveredNeighbors;
        connectedFringe = new LinkedHashSet<FringeNode>();
        connectedHint = new LinkedHashSet<HintNode>();
    }

    public List<FringeNode> getUndiscoveredFringe () {
        List<FringeNode> undiscoveredFringe = new ArrayList<FringeNode>();
        for (FringeNode fn : connectedFringe) {
            if (fn.state == UNDISCOVERED && !fn.isDeactivated) {
                undiscoveredFringe.add(fn);
            }
        }
        return undiscoveredFringe;
    }

    public ArrayList<int[]> getAllFlagCombinations () {
        int[] combination = new int[this.nbFlagToPlace];
        ArrayList<int[]> listCombination = new ArrayList<int[]>();
        generateFlagCombinations(0, this.nbFlagToPlace, this.getUndiscoveredFringe().size(), combination, listCombination);

        return listCombination;
    }

    public void generateFlagCombinations (int index, int nbFlag, int nbCase, int[] combinaison, ArrayList<int[]> listeC) {
        if (nbFlag == 0) {
            return;
        }
        if (index >= nbFlag) {

            int[] newCombinaison = combinaison.clone();
            listeC.add(newCombinaison);
            return;
        }
        int start = 0;
        if (index > 0) start = combinaison[index - 1] + 1;
        for (int i = start; i < nbCase; i++) {
            combinaison[index] = i;
            generateFlagCombinations(index + 1, nbFlag, nbCase, combinaison, listeC);
        }
    }


    /*
    * This method is use when the hint is satisfied and any more flag
    * put on his fringe would invalid him (Foward checking)
    * */
    public void deactivateAccessibleFringe () {

    }

    public boolean isUnsatisfiable () {
        updateSurroundingAwareness();
        return ((this.nbFlagToPlace < 0) ||
                (nbUndiscoveredNeighbors < nbFlagToPlace));
    }

    public boolean isSatisfied () {
        return this.nbFlagToPlace == 0;
    }

    /*
    * TODO je ne suis pas sur que ce soit safe
    * Il va regarder autour de lui les case qui on un flag
    * et celle qui sont encore non- decouverte
    * Il va ensuite updater ces varialbes
    * */
    public void updateSurroundingAwareness () {
        int nbFlagToPlace = value;
        int nbPlaceForFlag = 0;
        for (FringeNode fn : connectedFringe) {
            if (fn.state == FLAGED) {
                nbFlagToPlace--;
            } else if (fn.state == UNDISCOVERED && !fn.isDeactivated) {
                nbPlaceForFlag++;
            }
        }
        nbUndiscoveredNeighbors = nbPlaceForFlag;
        this.nbFlagToPlace = nbFlagToPlace;
    }

    public Set<FringeNode> getFlaggedFringe () {
        Set<FringeNode> flagged = new LinkedHashSet<FringeNode>();
        for (FringeNode fn : connectedFringe) {
            if (fn.state == FLAGED) {
                flagged.add(fn);
            }
        }
        return flagged;
    }

    public Set<FringeNode> getDeactivatedFringe () {
        Set<FringeNode> deactivatedFringe = new LinkedHashSet<FringeNode>();
        for (FringeNode fn : connectedFringe) {
            if (fn.isDeactivated) {
                deactivatedFringe.add(fn);
            }
        }
        return deactivatedFringe;
    }


    @Override
    public boolean equals (Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode () {
        return super.hashCode();
    }
}
