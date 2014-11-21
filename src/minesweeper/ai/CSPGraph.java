package minesweeper.ai;

import minesweeper.*;
import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.ai.utilCSP.Graph;
import minesweeper.ai.utilCSP.TimeOver;

import static minesweeper.Coup.*;

import static minesweeper.Case.*;

import java.util.*;

/**
 * Created by martin on 18/11/14.
 */
public class CSPGraph implements ArtificialPlayer {


    /*Timer*/
    private long timer;
    private long remain;
    private boolean END = false;
    private final int LIMITE = 10;


    private Grid gameGrid;
    private Set<Move> movesToPlay;

    //Set<Integer> undiscoveredFrontier;
    private List<Integer> nbMatchByFrontier;
    private Integer nbPossibilite = 0;


    Graph graph;

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {

        gameGrid = grid;
        Case[] copyGrid = grid.getCpyPlayerView();
        startTimer(delay);


        nbPossibilite = 0;
        movesToPlay = new HashSet<Move>();
        nbMatchByFrontier = new ArrayList<Integer>();



        /*
        * On lance le AI
        * nb: Ne faites pas attention au Try Catch c'est juste pour quitter la fonction si on timeout
        * */
        try {
            calculateMoves(grid);
        } catch (TimeOver ignored) {
            System.out.println("timeout");
        }




        /*
        * Une fois qu'on a fini d'analyser on va maintenant choisir les coup a jouer
        *
        * param: nbMatchByFrontier
        *           C'est dans cette liste que j'ai gardé le nombre de possibilité pour cette frontiere
        *           Par exemple un frontiere qui permet 10 combinaison différente de position de flags aura 10 possibilité
        *       fringeNodes
        *           C'est les cases non-decouverte qui cotoient une case découverte (un indice)
        *
        *
        * */
        if (movesToPlay.isEmpty()) {

            System.out.println("essai avec les resultats csp");
            for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
                List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);

                int nbPossibilityHere = nbMatchByFrontier.get(frontierIndex);
                for (Graph.FringeNode fn : fringeNodes) {

                    //Donc 0% des chances
                    if (fn.nbFlagHits == 0) {
                        movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));


                    /*
                    * Si ce noeud a recu un flag sur toute les dispositions valid de flags sur la frontiere
                    * Alors c'est qu'il y a 100% des chance d'avoir un flag
                    * */
                    } else if (fn.nbFlagHits == nbPossibilityHere) {
                        movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                    }
                }

            }

        }

        /*
        * Juste un check pour debugger
        * checkMove va renvoyer les Moves qui sont des erreurs (ex: un flag sur un case vide)
        * */
        Set<Move> errors = gameGrid.checkMove(movesToPlay);
        if (!errors.isEmpty()) {
            /*try{
                gameGrid.saveToFile(gameGrid.getATimeStampedGridName());
            }catch (Exception e){
                System.out.println(e);
            }*/

            System.out.println(" Problem and is timeout:" + (timeUp()) + "   grid is valid?:" + gameGrid.isValid());
        }

        if (timeUp()) {
            System.out.println("Time UP!");
        }


        /*
        * Si aucun coup sur a été trouvé alors on essai au hasard
        * */
        if (movesToPlay.isEmpty()) {
            List<Integer> legalMoves = new ArrayList<Integer>();
            for (int i = 0; i < grid.length; i++) {
                if (copyGrid[i] == UNDISCOVERED) {
                    legalMoves.add(i);
                }
            }

            Random ran = new Random();
            int index = legalMoves.get(ran.nextInt(legalMoves.size()));
            movesToPlay.add(new Move(index, Coup.SHOW));
        }

        return movesToPlay;
    }


    @Override
    public String getName() {
        return "CSP-Martin";
    }


    /**
     * C'est le AI en soit. Tout commence par cette method
     */
    void calculateMoves(Grid g) throws TimeOver {

        Case[] grid = g.getCpyPlayerView();

        /*
        * On commence par regarder si il y a des coup certain qu'on peut faire
        * */
        this.movesToPlay = g.checkForSafeMoves();


        //Si aucun coup certain trouvé alors on continue
        if (!movesToPlay.isEmpty()) {
            return;
        }

        /*
        * Va organiser les données dans un Graph
        * C'est plus ou moins un vrai graph. Il y a 2 Liste
        * allHintNode et allFringeNode
        * Ces deux listes contiennent toute les frontieres indépendante
        * allHintNode: Les frontieres qui sont des indice
        * allFringeNode: les frontieres qui sont des case non découvertes
        * */

        graph = new Graph(g);
        System.out.println("va pour le csp");
        int i = 0;
        //Cette boucle ca lancer le CSP sur une frontiere a la fois.
        for (List<Graph.HintNode> hintBorder : graph.allHintNode) {
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            i++;

            //Reset nbPossibilite pour cette frontiere
            nbPossibilite = 0;
            if (movesToPlay.isEmpty()) {
                recurseCSP(hintBorder, fringeNodes, 0); //   ON LANCE LE CSP SUR CETTE FRONTIERE!!


                //Juste un check pour debugger
            } else if (gameGrid.checkMove(movesToPlay).isEmpty()) {
                System.out.println("ne devrait pas");
            }

            nbMatchByFrontier.add(nbPossibilite);
        }

    }

    /*
    * C'est du CSP classique
    * Les cases avec des indices (hintNodes) sont l'équivalent des "variables" en CSP
    * On satisfait les variable une a une en priorisant celle qui sont côte a côte (selon l'heuristique 
    * du choix de la variable la plus contraignante)
    
     Etapes:
        1) check si les contraintes des variables ne sont pas violés ( exemple un indice de 2 est entourer de 3 flag)
            Si la cette configuration ne marche pas on backtrack
        2) check si on est au bout de la liste de variable. Si oui alors les flag placé sont compater et chaque case 
            de la frontiere incrément leur compter de flag si elle sont flaggé. (Pour calculer les probs)
        3) calcul de toutes les combinaison possible de placement de drapeau autour de la variable présente.
        4) pour chacune des combinaison trouvé. Les drapeau sont placées et on récurse.
    * */

    boolean recurseCSP(List<Graph.HintNode> hintNodes, List<Graph.FringeNode> fringeNodes, int index) throws TimeOver {

        //Si on dépasse le thinkDelay
        if (timeUp()) {
            throw new TimeOver();
        }

        /*
        * Vérifie si jusqu`a maintenant toute les variables (la frontiere avec les indices) sont satisfaites

        */

        if (!allFlagsOkay(hintNodes, index)) {
            return false;
        }


        /*
        * Quand on est arrivé au bout de la frontiere et que tout marche!
        * Une disposition(solution CSP) est trouvé!
        * */
        if (index >= hintNodes.size()) {
            /*
            * on passe sur tout les nodes et on check si il y un flag.
            * Si oui alors on incrément le compteur de flags
            *   C'est ce qui sera utiliser pour les probabilité. Si le compteur est a 5 et le nbPossiblité a 10 alors ce node a 50% d'avoir un flag
            * */
            for (Graph.FringeNode fn : fringeNodes) {
                if (fn.state == FLAGED) {
                    fn.nbFlagHits++;
                }
            }
            //Puisqu'on a trouvé une disposition valide on incrément le nombre de possibilité pour cette frontiere
            nbPossibilite++;
            return true;
        }


        //On passe a la prochaine variable a satisfaire
        Graph.HintNode variableToSatisfy = hintNodes.get(index);
        // Update la variable puisqu'on a peut etre flag certainte de ses cases autour.
        variableToSatisfy.updateSurroundingAwareness();

        //Un check pour voir si la variable a trop de flag autour d'elle on backtrack
        if (variableToSatisfy.nbFlagToPlace < 0) {
            return false;
        }
        //Si la variable est déja satisfaite alors on passe a la suivante!
        if (variableToSatisfy.nbFlagToPlace == 0) {
            return recurseCSP(hintNodes, fringeNodes, index + 1);
        }


        //On va chercher cases non decouvertes voisin
        Set<Graph.FringeNode> neighborsFringe = variableToSatisfy.connectedFringe;
        List<Graph.FringeNode> undiscoveredFringe = new ArrayList<Graph.FringeNode>();
        for (Graph.FringeNode fn : neighborsFringe) {
            if (fn.state == UNDISCOVERED) {
                undiscoveredFringe.add(fn);
            }
        }



        /*
        * Okey ca c'est peut etre un peu tricky
        * Mon but est d'avoir tout les combinaisons du nombre de flags qui me reste a placer pour cette variables sur
        * les cases disponibles autour d'elle
        *
        *Ce que vont faire ces 2 ou 3 prochaine ligne c'est : http://fr.wikipedia.org/wiki/Combinaison_(math%C3%A9matiques)
        *
        *
        * */
        int[] combination = new int[variableToSatisfy.nbFlagToPlace];
        ArrayList<int[]> listcombination = new ArrayList<int[]>();
        combinaisonFlag(0, variableToSatisfy.nbFlagToPlace, undiscoveredFringe.size()
                , combination, listcombination);



        /*
        * Itere sur les combinaisons trouvé
        * */
        for (int[] oneCombination : listcombination) {

            //Garder en  memoire le nb de flag a placer ici parce que variableToSatisfy va changer au moment de recurser
            int nbFlagToPlaceHere = variableToSatisfy.nbFlagToPlace;

            /*
            * On place les flags sur cases
            * */
            for (int i = 0; i < nbFlagToPlaceHere; i++) {
                Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
                fringeToFlag.state = FLAGED;
            }
            //CSP pour la prochaine variable!
            recurseCSP(hintNodes, fringeNodes, index + 1);

            /*
            * On retire les flags préalablement posé
            * */
            for (int i = 0; i < nbFlagToPlaceHere; i++) {
                Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
                fringeToFlag.state = UNDISCOVERED;
            }
        }

        return false;
    }


    boolean allFlagsOkay(List<Graph.HintNode> hintNodes, int nbDone) {
        for (int i = 0; i < nbDone; i++) {
            Graph.HintNode hintNode = hintNodes.get(i);
            int value = hintNode.value;

            Set<Graph.FringeNode> neighborsFringe = hintNode.connectedFringe;

            int nbFlag = 0;
            for (Graph.FringeNode fn : neighborsFringe) {
                if (fn.state == FLAGED) {
                    nbFlag++;
                }
            }
            if (nbFlag != value) {
                return false;
            }

        }
        return true;
    }


    /*
    * C'est simlement l'implementation de C(n,p) exemple : http://calculis.net/combinaison
    * nbFlag  = p
    * nbCase = n
    * EXEMPLE:
    * Si  : nbFlag = 2 et nbCase= 3  -> C(2,3)
    * voici ce qui sera retourné dans listC
    *  [0, 1],
    *  [0, 2],
    *  [1, 2]
    * */
    void combinaisonFlag(int index, int nbFlag, int nbCase, int[] combinaison, ArrayList<int[]> listeC) {
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
            combinaisonFlag(index + 1, nbFlag, nbCase, combinaison, listeC);
        }
    }

    private void startTimer(int delai) {
        END = false;
        timer = System.currentTimeMillis();
        remain = delai;
    }


    private String showTimeRemain() {
        return ("Time: " + (remain - (System.currentTimeMillis() - timer)) + " ms");
    }

    /**
     * @return Retourne le temps restant
     */


    long timeRemaining() {
        long passed = (System.currentTimeMillis() - timer);
        return remain - passed;
    }

    /**
     * Indique si le temps est écoulé
     *
     * @return true si temps écoulé
     */
    boolean timeUp() {
        if (END) {
            return true;
        }

        if (timeRemaining() < LIMITE) {
            END = true;
            return true;
        }

        return false;
    }

}
