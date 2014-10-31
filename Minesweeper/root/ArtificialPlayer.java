package root;

import java.util.Set;

/**
 * Created by MB on 10/30/2014.
 */
public interface ArtificialPlayer {


    public Set<Move> getAiPlay(Grid g);
    public String getAiName ();

}
