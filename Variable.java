// Variable.java
package cs2110;

import java.util.Set;

/**
 * Represents a named variable in an expression.
 */
public class Variable implements Expression {

    /** The name of this variable. */
    private final String name;

    /**
     * Constructs a Variable node with the specified name.
     *
     * @param name the name of the variable; must not be empty
     */
    public Variable(String name) {
        assert name != null && !name.isEmpty();
        this.name = name;
    }



    /**
     * Returns the value of this variable from the provided VarTable.
     *
     * @param vars the table containing variable values
     * @return the value of this variable
     * @throws UnboundVariableException if the variable is not in the VarTable
     */
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        return vars.get(name);
    }

    /**
     * Returns the number of operations for this Variable, which is zero.
     */
    @Override
    public int opCount() {
        return 0;
    }

    /**
     * Returns the infix string representation of this Variable, which is simply its name.
     */
    @Override
    public String infixString() {
        return name;
    }

    /**
     * Returns the postfix string representation of this Variable, which is simply its name.
     */
    @Override
    public String postfixString() {
        return name;
    }

    /**
     * Returns the set of variable names for this Variable, which is a set containing only this
     * variable's name.
     */
    @Override
    public Set<String> dependencies() {
        return Set.of(name);
    }

    /**
     * Returns a new Variable node optimized based on the given VarTable.
     * If this variable has a constant value in the table, returns a Constant node with that value.
     * Otherwise, returns this Variable node.
     */
    @Override
    public Expression optimize(VarTable vars) {
        assert vars != null;
        if (vars.contains(name)) {
            try {
                return new Constant(vars.get(name));
            } catch (UnboundVariableException e) {
                throw new RuntimeException(e); // Should not happen if contains(name) is true
            }
        }
        return this;
    }

    /**
     * Checks if this Variable is equal to another object.
     * Two Variable nodes are equal if they have the same name.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Variable variable = (Variable) other;
        return name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Variable(name=" + name + ")";
    }
}
