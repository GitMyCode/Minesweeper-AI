package root;

import java.util.EventListener;
import java.util.Set;

/**
 * Created by MB on 10/29/2014.
 */
public interface GridController extends EventListener {

    public void caseClicked(int ligne, int colonne);
    public void caseClicked(int indexCase);

    public void movesSetPlay(Set<Move> moves);




}
