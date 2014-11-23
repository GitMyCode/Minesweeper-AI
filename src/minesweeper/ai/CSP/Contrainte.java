package minesweeper.ai.CSP;

import java.util.Set;

public abstract class Contrainte {

    private Set<Variable> variables;

    public abstract boolean isViolated();
 
}
