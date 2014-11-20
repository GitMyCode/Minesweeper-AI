public abstract class CSP {

    public Set<Variable> backtrackingSearch(List<Variable> variables);
    private Set<Variable> backtrackingSearch(List<Variable> assigned, List<Variable> unassigned);

    private Variable selectUnassignedVariable(List<Variable> unassigned);

}
