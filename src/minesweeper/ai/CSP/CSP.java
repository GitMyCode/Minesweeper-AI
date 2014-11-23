package minesweeper.ai.CSP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CSP {

    public Set<Variable> backtrackingSearch(List<Variable> variables) {
        return backtrackingSearch(new ArrayList<Variable>(), variables);
    }

    private Set<Variable> backtrackingSearch(List<Variable> assigned, List<Variable> unassigned) {
        if (unassigned.isEmpty()) return assigned;
        return new HashSet<Variable>();
    }

    protected abstract Variable selectUnassignedVariable(List<Variable> unassigned);
}
