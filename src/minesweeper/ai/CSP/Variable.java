package minesweeper.ai.CSP;

import java.util.ArrayList;
import java.util.Set;

public abstract class Variable {

    private Set<Constraint> constraints;

    protected abstract boolean assignementIsValid();
    protected abstract boolean isAssigned();
    protected abstract void assign(Object value);
    protected abstract ArrayList<Object> orderDomainValues();

}
