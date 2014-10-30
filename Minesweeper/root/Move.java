package root; /**
 * Created by MB on 10/30/2014.
 */

import root.ENUM.COUP;

public class Move {

    public COUP coup;
    public int index;

    public Move(int index, COUP coup){
        this.coup = coup;
        this.index = index;
    }
}
