package root.ai.utilCSP;

import root.ENUM.CASE;
import root.ENUM.COUP;
import root.Move;

import java.util.List;
import java.util.Set;

/**
 * Created by MB on 11/1/2014.
 */
public class Util {



    public static void printGrid(CASE[] grid,int nbcol){
        String print="";

        int i=1;
        for(CASE c : grid){
            if(c == CASE.UNDISCOVERED){
                print+= '■';
            }else if(c == CASE.EMPTY) {
                print+='_';
            }else if(c == CASE.FLAGED){
                print+= '⚑';

            }else if(c == CASE.BLOW) {
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

    public static void printAllCoup(CASE[] gridOriginal, int nbcol,Set<Move> coups){

        String print="";

        int i=1;
        CASE[] grid = gridOriginal.clone();

        for(Move m : coups){
            if(m.coup == COUP.SHOW){

                grid[m.index] = CASE.BLOW; //Just pour une valeur quelquonc
            }else if(m.coup == COUP.FLAG){
                grid[m.index] = CASE.DEFUSED; //Just pour une valeur quelquonc
            }
        }


        for(CASE c : grid){
            if(c == CASE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASE.EMPTY) {
                print+='_';
            }else if(c == CASE.FLAGED){
                print+= '⚑';

            }else if(c == CASE.BLOW) {
                print+= '●';
            }else if(c== CASE.DEFUSED){
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

    public static void printFrontiereInOrder(CASE[] gridOrigin,int nbcol, List<Integer> frontiere){
        CASE[] cpy = gridOrigin.clone();
        for(Integer i: frontiere){
            printIndex(cpy,nbcol,i);
        }
    }

    public static void printFrontiere(CASE[] gridOrigin, int nbcol,Set<Integer> frontiere){

        String print="";

        int i=1;
        CASE[] grid = gridOrigin.clone();

        for(Integer f : frontiere){
            grid[f] = CASE.BLOW;
        }


        for(CASE c : grid){
            if(c == CASE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASE.EMPTY) {
                print+='_';
            }else if(c == CASE.FLAGED){
                print+= '⚑';

            }else if(c == CASE.BLOW) {
                print+= '●';
            }else if(c== CASE.DEFUSED){
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

    public static void printFrontiere(CASE[] gridOrigin, int nbcol,List<Integer> frontiere){

        String print="";

        int i=1;
        CASE[] grid = gridOrigin.clone();

        for(Integer f : frontiere){
            grid[f] = CASE.BLOW;
        }


        for(CASE c : grid){
            if(c == CASE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASE.EMPTY) {
                print+='_';
            }else if(c == CASE.FLAGED){
                print+= '⚑';

            }else if(c == CASE.BLOW) {
                print+= '●';
            }else if(c== CASE.DEFUSED){
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

        public static void printIndex(CASE[] gridOrigin, int nbcol,Integer toShow){

        String print="";

        int i=1;
        CASE[] grid = gridOrigin.clone();

            grid[toShow] = CASE.BLOW;


        for(CASE c : grid){
            if(c == CASE.UNDISCOVERED){
                print+= '□';
            }else if(c == CASE.EMPTY) {
                print+='_';
            }else if(c == CASE.FLAGED){
                print+= '⚑';

            }else if(c == CASE.BLOW) {
                print+= '●';
            }else if(c== CASE.DEFUSED){
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
