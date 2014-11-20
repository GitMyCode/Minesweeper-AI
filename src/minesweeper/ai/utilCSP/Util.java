package minesweeper.ai.utilCSP;

import minesweeper.ENUM.CASEGRILLE;
import minesweeper.ENUM.COUP;
import minesweeper.Move;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Projet de joueur artificiel de Minesweeper avec différents algorithmes
 * Dans le cadre du 3e TP en Intelligence Artificielle (INF4230)
 *
 * Automne 2014
 * Par l'équipe:
 *   Martin Bouchard
 *   Frédéric Vachon
 *   Louis-Bertrand Varin
 *   Geneviève Lalonde
 *   Nilovna Bascunan-Vasquez
 */
class Util {
    public static void printGrid(CASEGRILLE[] grid,int nbcol){
        String print="";

        int i=1;
        for(CASEGRILLE c : grid){
            if(c == CASEGRILLE.UNDISCOVERED){
                print+= '■';
            }else if(c == CASEGRILLE.EMPTY) {
                print+='_';
            }else if(c == CASEGRILLE.FLAGED){
                print+= '⚑';

            }else if(c == CASEGRILLE.BLOW) {
                print+= '●';
            }else {
                print += c.indexValue;
            }
            if(i%nbcol ==0 && i!=0){
                print += "\n";
            }
            i++;
        }
        System.out.println(print);
    }

    public static void printAllCoup(CASEGRILLE[] gridOriginal, int nbcol,Set<Move> coups){

        String print="";

        int i=1;
        CASEGRILLE[] grid = gridOriginal.clone();

        for(Move m : coups){
            if(m.coup == COUP.SHOW){

                grid[m.index] = CASEGRILLE.BLOW; //Just pour une valeur quelquonc
            }else if(m.coup == COUP.FLAG){
                grid[m.index] = CASEGRILLE.DEFUSED; //Just pour une valeur quelquonc
            }
        }


        for(CASEGRILLE c : grid){
            if(c == CASEGRILLE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASEGRILLE.EMPTY) {
                print+='_';
            }else if(c == CASEGRILLE.FLAGED){
                print+= '⚑';

            }else if(c == CASEGRILLE.BLOW) {
                print+= '●';
            }else if(c== CASEGRILLE.DEFUSED){
                print+= '◯'; //○
            }else {
                print += c.indexValue;
            }
            if(i%nbcol ==0 && i!=0){
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }

    public static void printFrontiereInOrder(CASEGRILLE[] gridOrigin,int nbcol, List<Integer> frontiere){
        CASEGRILLE[] cpy = gridOrigin.clone();
        for(Integer i: frontiere){
            printIndex(cpy,nbcol,i);
        }
    }

    public static void printFrontiereNodeInOrder(CASEGRILLE[] gridOrigin,int nbcol, List<Graph.Node> frontiere){
        CASEGRILLE[] cpy = gridOrigin.clone();
        for(Graph.Node i: frontiere){
            printIndex(cpy,nbcol,i.indexInGrid);
        }
    }

    public static void printFrontiere(CASEGRILLE[] gridOrigin, int nbcol,Set<Integer> frontiere){

        String print="";

        int i=1;
        CASEGRILLE[] grid = gridOrigin.clone();

        for(Integer f : frontiere){
            grid[f] = CASEGRILLE.BLOW;
        }


        for(CASEGRILLE c : grid){
            if(c == CASEGRILLE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASEGRILLE.EMPTY) {
                print+='_';
            }else if(c == CASEGRILLE.FLAGED){
                print+= '⚑';

            }else if(c == CASEGRILLE.BLOW) {
                print+= '●';
            }else if(c== CASEGRILLE.DEFUSED){
                print+= '◯'; //○
            }else {
                print += c.indexValue;
            }
            if(i%nbcol ==0 && i!=0){
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }

    public static void printFrontiereNode(CASEGRILLE[] gridOrigin, int nbcol,List<? extends Graph.Node> frontiere){

        String print="";

        int i=1;
        CASEGRILLE[] grid = gridOrigin.clone();

        for(Graph.Node f : frontiere){
            Graph.Node fn =  Graph.Node.class.cast(f);
            grid[fn.indexInGrid] = CASEGRILLE.BLOW;
        }


        for(CASEGRILLE c : grid){
            if(c == CASEGRILLE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASEGRILLE.EMPTY) {
                print+='_';
            }else if(c == CASEGRILLE.FLAGED){
                print+= '⚑';

            }else if(c == CASEGRILLE.BLOW) {
                print+= '●';
            }else if(c== CASEGRILLE.DEFUSED){
                print+= '◯'; //○
            }else {
                print += c.indexValue;
            }
            if(i%nbcol ==0 && i!=0){
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }


    public static void printFrontiere(CASEGRILLE[] gridOrigin, int nbcol,List<Integer> frontiere){

        String print="";

        int i=1;
        CASEGRILLE[] grid = gridOrigin.clone();

        for(Integer f : frontiere){
            grid[f] = CASEGRILLE.BLOW;
        }


        for(CASEGRILLE c : grid){
            if(c == CASEGRILLE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASEGRILLE.EMPTY) {
                print+='_';
            }else if(c == CASEGRILLE.FLAGED){
                print+= '⚑';

            }else if(c == CASEGRILLE.BLOW) {
                print+= '●';
            }else if(c== CASEGRILLE.DEFUSED){
                print+= '◯'; //○
            }else {
                print += c.indexValue;
            }
            if(i%nbcol ==0 && i!=0){
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }
    public static void printIndex(CASEGRILLE[] gridOrigin, int nbcol, Integer toShow){

        String print="";

        int i=1;
        CASEGRILLE[] grid = gridOrigin.clone();

            grid[toShow] = CASEGRILLE.BLOW;


        for(CASEGRILLE c : grid){
            if(c == CASEGRILLE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASEGRILLE.EMPTY) {
                print+='_';
            }else if(c == CASEGRILLE.FLAGED){
                print+= '⚑';

            }else if(c == CASEGRILLE.BLOW) {
                print+= '●';
            }else if(c== CASEGRILLE.DEFUSED){
                print+= '◯'; //○
            }else {
                print += c.indexValue;
            }
            if(i%nbcol ==0 && i!=0){
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }


}
