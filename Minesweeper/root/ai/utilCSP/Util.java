package root.ai.utilCSP;

import root.ENUM.CASE;

/**
 * Created by MB on 11/1/2014.
 */
public class Util {



    public static void printGrid(CASE[] grid,int nbcol){
        String print="";
        for(int i=0;i<grid.length;i++){
            print += grid[i];
            if(i%nbcol ==0){
                print += "\n";
            }
        }

        System.out.println(print);

    }


}
