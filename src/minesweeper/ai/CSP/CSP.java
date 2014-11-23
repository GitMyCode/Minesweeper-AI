package minesweeper.ai.CSP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CSP {

    public Set<Variable> backtrackingSearch(List<Variable> variables) {

        Set<Variable> result = backtrackingSearch(new ArrayList<Variable>(), variables);
        if (result == null) return new HashSet<Variable>();
        return result;
    }

    private Set<Variable> backtrackingSearch(List<Variable> assigned, List<Variable> unassigned) {
        if (unassigned.isEmpty()) return new HashSet<Variable>(assigned);
        Variable toAssign = selectUnassignedVariable(unassigned);

        for (Object value: toAssign.orderDomainValues()) {
            if (checkValueConsistency(value, assigned)) {
                toAssign.assign(value);
                assigned.add(toAssign);
                Set<Variable> result = backtrackingSearch(assigned, unassigned);
                if (result != null) return result;
                unassigned.remove(toAssign);
            }
        }

        return null;
    }

    protected abstract boolean checkValueConsistency(Object value, List<Variable> assignment);
    protected abstract Variable selectUnassignedVariable(List<Variable> unassigned);
}
