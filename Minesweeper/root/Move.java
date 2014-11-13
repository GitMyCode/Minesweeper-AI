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


    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (index != move.index) return false;
        if (coup != move.coup) return false;

        return true;
    }


    @Override
    public int hashCode () {
        int result = coup != null ? coup.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }
}
