package minesweeper.ai.utilCSP;

import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.Move;

import java.util.List;
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
    public static void printGrid(Case[] grid,int nbcol){
        String print="";

        int i=1;
        for(Case c : grid){
            if(c == Case.UNDISCOVERED){
                print+= '■';
            }else if(c == Case.EMPTY) {
                print+='_';
            }else if(c == Case.FLAGED){
                print+= '⚑';

            }else if(c == Case.BLOW) {
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

    public static void printAllCoup(Case[] gridOriginal, int nbcol,Set<Move> coups){

        String print="";

        int i=1;
        Case[] grid = gridOriginal.clone();

        for(Move m : coups){
            if(m.coup == Coup.SHOW){

                grid[m.index] = Case.BLOW; //Just pour une valeur quelquonc
            }else if(m.coup == Coup.FLAG){
                grid[m.index] = Case.DEFUSED; //Just pour une valeur quelquonc
            }
        }


        for(Case c : grid){
            if(c == Case.UNDISCOVERED){
                print+= '□';
            }else if(c == Case.EMPTY) {
                print+='_';
            }else if(c == Case.FLAGED){
                print+= '⚑';

            }else if(c == Case.BLOW) {
                print+= '●';
            }else if(c== Case.DEFUSED){
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

    public static void printFrontiereInOrder(Case[] gridOrigin,int nbcol, List<Integer> frontiere){
        Case[] cpy = gridOrigin.clone();
        for(Integer i: frontiere){
            printIndex(cpy,nbcol,i);
        }
    }

    public static void printFrontiereNodeInOrder(Case[] gridOrigin,int nbcol, List<Graph.Node> frontiere){
        Case[] cpy = gridOrigin.clone();
        for(Graph.Node i: frontiere){
            printIndex(cpy,nbcol,i.indexInGrid);
        }
    }

    public static void printFrontiere(Case[] gridOrigin, int nbcol,Set<Integer> frontiere){

        String print="";

        int i=1;
        Case[] grid = gridOrigin.clone();

        for(Integer f : frontiere){
            grid[f] = Case.BLOW;
        }


        for(Case c : grid){
            if(c == Case.UNDISCOVERED){
                print+= '□';
            }else if(c == Case.EMPTY) {
                print+='_';
            }else if(c == Case.FLAGED){
                print+= '⚑';

            }else if(c == Case.BLOW) {
                print+= '●';
            }else if(c== Case.DEFUSED){
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

    public static void printFrontiereNode(Case[] gridOrigin, int nbcol,List<? extends Graph.Node> frontiere){

        String print="";

        int i=1;
        Case[] grid = gridOrigin.clone();

        for(Graph.Node f : frontiere){
            Graph.Node fn =  Graph.Node.class.cast(f);
            grid[fn.indexInGrid] = Case.BLOW;
        }


        for(Case c : grid){
            if(c == Case.UNDISCOVERED){
                print+= '□';
            }else if(c == Case.EMPTY) {
                print+='_';
            }else if(c == Case.FLAGED){
                print+= '⚑';

            }else if(c == Case.BLOW) {
                print+= '●';
            }else if(c== Case.DEFUSED){
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


    public static void printFrontiere(Case[] gridOrigin, int nbcol,List<Integer> frontiere){

        String print="";

        int i=1;
        Case[] grid = gridOrigin.clone();

        for(Integer f : frontiere){
            grid[f] = Case.BLOW;
        }


        for(Case c : grid){
            if(c == Case.UNDISCOVERED){
                print+= '□';
            }else if(c == Case.EMPTY) {
                print+='_';
            }else if(c == Case.FLAGED){
                print+= '⚑';

            }else if(c == Case.BLOW) {
                print+= '●';
            }else if(c== Case.DEFUSED){
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
    public static void printIndex(Case[] gridOrigin, int nbcol, Integer toShow){

        String print="";

        int i=1;
        Case[] grid = gridOrigin.clone();

            grid[toShow] = Case.BLOW;


        for(Case c : grid){
            if(c == Case.UNDISCOVERED){
                print+= '□';
            }else if(c == Case.EMPTY) {
                print+='_';
            }else if(c == Case.FLAGED){
                print+= '⚑';

            }else if(c == Case.BLOW) {
                print+= '●';
            }else if(c== Case.DEFUSED){
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
