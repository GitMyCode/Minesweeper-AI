package minesweeper.ai.dataRepresentation;

import minesweeper.Case;
import minesweeper.Direction;
import minesweeper.Grid;

import static minesweeper.Case.*;
import static minesweeper.Direction.*;

import java.util.*;

/**
 * Created by martin on 17/11/14.
 */
public class Graph {

    public int nbFrontiere = 0;

    public HashMap<Integer, HintNode> knownHintNodes;
    public HashMap<Integer, FringeNode> knownFringeNodes;
    public List<List<HintNode>> allHintNode;
    public List<List<FringeNode>> allFringeNodes;
    public List<Integer> nbValidAssignationsPerFrontier;
    public List<Integer> nbMinimalAssignementsPerFrontier;


    public Set<Integer> deactivatedNode;
    public Grid gameGrid;
    public Case[] caseGrille;

    public Graph(Grid gameGrid) {
        this.gameGrid = gameGrid;
        caseGrille = gameGrid.getCpyPlayerView();

        deactivatedNode = new HashSet<Integer>();
        knownHintNodes = new HashMap<Integer, HintNode>();
        knownFringeNodes = new HashMap<Integer, FringeNode>();

        allHintNode = new ArrayList<List<HintNode>>();
        allFringeNodes = new ArrayList<List<FringeNode>>();
        nbValidAssignationsPerFrontier = new ArrayList<Integer>();
        nbMinimalAssignementsPerFrontier = new ArrayList<Integer>();

        lookForInvalidFringeNode(caseGrille);
        findFrontier(caseGrille);

        nbFrontiere = allHintNode.size();

    }


    /*
    * Va chercher les case qui sont entouré de case non-decouvert
    * C'est juste pour eviter que les frontieres fassent des détours
    * */
    void lookForInvalidFringeNode(Case[] grid) {
        for (Integer i = 0; i < grid.length; i++) {
            if (Case.isIndicatorCase(grid[i])) {
                if (gameGrid.getUndiscoveredneighbours(i).size() == 8) {
                    //deactivatedNode.add(i);
                }
            }
        }
    }

    /*
    * Va chercher les frontiers qui sont independantes
    * */
    void findFrontier(Case[] grid) {
        //Un set pour s'assurer qu'on ne prend pas deux fois le meme noeud;
        for (int i = 0; i < grid.length; i++) {

            if (!knownFringeNodes.containsKey(i) && gameGrid.isAFringeNode(i)) {
                FringeNode fringeNode = new FringeNode(i);

                //Va chercher les indices qui influence ce Noeud
                fringeNode.hintNodes = getHintNeirbour(grid, fringeNode);

                List<FringeNode> front = new ArrayList<FringeNode>();
                front.add(fringeNode);
                knownFringeNodes.put(i, fringeNode);

                //On ce lance dans la recursion pour accumuler les nodes suivant
                putInFrontier(fringeNode, front, grid);

                //Ajoute la frontier accumuler durant la recursion aux Nodes déja visité
                //Je contraint les frontiers a etre au moins plus que 2 sinon on ne peut pas faire grand chose
                //avec ca. Mais c'est peut etre une mauvaise idée
                if (front.size() >= 2) {
                    allFringeNodes.add(front); // Un nouvelle frontiere a été trouver. On l'ajoute
                }
            }
        }

        /*
        * Dans certain cas on obtiens des frontieres qui sont lié a 1 seul
         * indice et on ne peut rien faire avec ca donc il faut les supprimer.
         * Cette liste garde les references des liste a effacer
        * */
        List<List<FringeNode>> fringesToRemove = new ArrayList<List<FringeNode>>();

        /*
        * Une fois qu'on a tout les frontieres des case-non decouvertes
        * on veut aussi les indices qui cotoient ces frontieres dans des listes
        * */
        for (List<FringeNode> l : allFringeNodes) {
            Set<HintNode> hintNodes = new LinkedHashSet<HintNode>();
            for (FringeNode fn : l) {
                for (HintNode hn : fn.hintNodes) {
                    hn.connectedHint.addAll(fn.hintNodes);
                    hn.connectedHint.remove(hn);
                }
                //la liste des indices qui influence ce noeud
                hintNodes.addAll(fn.hintNodes);
            }

            //Pour une frontiere acceptable il faut au moins 2 indices (sinon on peut pas vraiment faire de probabilité)
            if (hintNodes.size() >= 1) {
                allHintNode.add(new ArrayList<HintNode>(hintNodes));
            } else {
                fringesToRemove.add(l);
            }
        }
        //Efface les frontieres qui n'on qu'un seul indice
        allFringeNodes.removeAll(fringesToRemove);


    }

    /*
    * Methode qui recurse sur les noeuds et accumuler les nouveau qu'il trouve
    *
    * */
    void putInFrontier(FringeNode startNode, List<FringeNode> fringe, Case[] grid) {

        Queue queue = new LinkedList();
        queue.add(startNode);

        knownFringeNodes.put(startNode.indexInGrid, startNode);

        FringeNode lastNode = startNode;


        while (!queue.isEmpty()) {
            FringeNode currentNode = (FringeNode) queue.remove();

            FringeNode nextNode = null;
             /*Va chercher les prochains direction disponible (qui menent a un noeud non visite)*/

            Set<Direction> thisDirection = getPossibleDirection(grid, currentNode.indexInGrid);
            //Si aucun direction alors on backtrack

            if (!(thisDirection == null || thisDirection.isEmpty())) {
                for (Direction nextDirection : thisDirection) {
                    int next = currentNode.indexInGrid + gameGrid.step(nextDirection);


                    if (!knownFringeNodes.containsKey(next)) {
                        nextNode = new FringeNode(next);
                        nextNode.hintNodes = getHintNeirbour(grid, nextNode);


                        knownFringeNodes.put(nextNode.indexInGrid, nextNode);
                        queue.add(nextNode);
                        fringe.add(nextNode);
                    }
                }
            }
        }
    }


    /*
    * Va aller chercher les prochaines directions qui menent vers un noeud valide
    * */
    Set<Direction> getPossibleDirection(Case[] grid, int index) {
        Set<Direction> directions = new LinkedHashSet<Direction>();

        for (Direction D : HUIT_DIRECTIONS) {
            int next = index + gameGrid.step(D);


            if (gameGrid.isStepThisDirInGrid(D, index) &&
                    !knownFringeNodes.containsKey(next) &&
                    gameGrid.isAFringeNode(next)) {
                Collection<Integer> indiceNeirboursCurrentNode = getIndiceNeirbours(grid, index);
                Collection<Integer> indiceNeirboursNextNode = getIndiceNeirbours(grid, next);

                //Check si les deux noeud partage un indice (Et donc s'influence)
                if (!Collections.disjoint(indiceNeirboursCurrentNode, indiceNeirboursNextNode)) {

                    directions.add(D);
                }

            }
        }
        return directions;

    }

    /*
    * Retourne les cases voisines qui sont des index
    * */
    Set<Integer> getIndiceNeirbours(Case[] grid, int index) {
        Set<Integer> indices = new LinkedHashSet<Integer>();
        for (Integer i : gameGrid.getSurroundingIndex(index)) {
            if (Case.isIndicatorCase(grid[i]) && !deactivatedNode.contains(i)) {
                indices.add(i);
            }
        }
        return indices;
    }

    public LinkedHashSet<HintNode> getHintNeirbour(Case[] g, FringeNode fringeNode) {

        LinkedHashSet<HintNode> hintNodes = new LinkedHashSet<HintNode>();
        for (Integer indexHint : gameGrid.getSurroundingIndex(fringeNode.indexInGrid)) {

            if (Case.isIndicatorCase(g[indexHint])) {
                HintNode hn = null;
                if (knownHintNodes.containsKey(indexHint)) {
                    hn = knownHintNodes.get(indexHint);
                } else {
                    hn = new HintNode(indexHint, this.gameGrid.countUnplacedFlags(indexHint), this.gameGrid.getUndiscoveredneighbours(indexHint).size());
                    knownHintNodes.put(indexHint, hn);
                }
                hintNodes.add(hn);
                hn.connectedFringe.add(fringeNode);
            }

        }
        return hintNodes;

    }


}
