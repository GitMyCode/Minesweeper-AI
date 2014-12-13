package minesweeper.ai.dataRepresentation;

public class Node {

    public int indexInGrid;

    Node(int indexInGrid) {
        this.indexInGrid = indexInGrid;
    }

    @Override
    public boolean equals(Object obj) {

            /*
            * Tres wierd et certainement pas comforme aux bonnes pratiques.
            * Permet de comparer un Node avec un Integer
            * */
        if (obj.getClass() == Integer.class) {
            return (indexInGrid == (Integer) obj);
        }

        Node other = (Node) obj;

        if (indexInGrid == other.indexInGrid) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return indexInGrid;
    }
}
