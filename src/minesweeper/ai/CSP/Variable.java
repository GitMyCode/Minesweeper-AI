package minesweeper.ai.CSP;

import java.util.Set;

public abstract class Variable {

    private Set<Contrainte> constraints;

    public abstract boolean assignementIsValid();
    public abstract boolean isAssigned();

}
