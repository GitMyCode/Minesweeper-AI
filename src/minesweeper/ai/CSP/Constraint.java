package minesweeper.ai.CSP;

import java.util.Set;

public abstract class Constraint {

    private Set<Variable> variables;

    public abstract boolean isViolated();
 
}
