package minesweeper.ai.dataRepresentation;

import minesweeper.Case;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import static minesweeper.Case.UNDISCOVERED;

public class FringeNode extends Node implements Comparable {

    public float probabilityMine = 0.5f;
    public int nbFlagsHit = 0;
    public LinkedHashSet<HintNode> hintNodes;
    public LinkedList<FringeNode> fringeNeighbor;
    public Case state = UNDISCOVERED;
    public boolean isDeactivated = false;
    public TreeSet<Integer> combinationsUsed = new TreeSet<Integer>();

    public FringeNode(int index) {
        super(index);
        hintNodes = new LinkedHashSet<HintNode>();
        fringeNeighbor = new LinkedList<FringeNode>();
    }

    public void computeMineProbability(int totalAssignations) {
        this.probabilityMine = (float) this.nbFlagsHit / totalAssignations;
    }

    public boolean isObviousMine() {
        return this.probabilityMine == 1;
    }

    public boolean isSafe() {
        return this.probabilityMine == 0;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        FringeNode other = (FringeNode) o;
        if (this.probabilityMine < other.probabilityMine) {
            return -1;
        } else if (this.probabilityMine > other.probabilityMine) {
            return 1;
        }
        return 0;
    }

    public String toString() {
        return "Probability of Mine : " + (this.probabilityMine * 100) + "%";
    }
}
