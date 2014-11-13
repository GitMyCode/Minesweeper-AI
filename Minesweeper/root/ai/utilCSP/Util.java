package root.ai.utilCSP;

import root.ENUM.CASEGRILLE;
import root.ENUM.COUP;
import root.Move;

import java.util.List;
import java.util.Set;

/**
 * Created by MB on 11/1/2014.
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

        private static void printIndex(CASEGRILLE[] gridOrigin, int nbcol, Integer toShow){

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
