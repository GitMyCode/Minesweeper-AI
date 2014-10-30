package root;

import java.util.EventListener;

/**
 * Created by MB on 10/29/2014.
 */
public interface GridController extends EventListener {

    public void caseClicked(int ligne, int colonne);
    public void caseClicked(int indexCase);


}
