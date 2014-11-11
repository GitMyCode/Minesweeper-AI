package root;

import java.util.Set;

public interface ArtificialPlayer {
    public Set<Move> getNextMoves(Grid g, int thinkLimit);
    public String getName();
}
