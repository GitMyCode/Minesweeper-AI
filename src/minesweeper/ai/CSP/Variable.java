package minesweeper.ai.CSP;

import java.util.Set;

public abstract class Variable {

    private Set<Constraint> constraints;

    public abstract boolean assignementIsValid();
    public abstract boolean isAssigned();

}
