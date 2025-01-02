// Conditional.java
package cs2110;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a conditional expression that selects between two branches based on a condition.
 */
public class Conditional implements Expression {

    /** The condition expression that determines which branch to evaluate. */
    private final Expression condition;

    /** The true branch expression to evaluate if the condition is non-zero. */
    private final Expression trueBranch;

    /** The false branch expression to evaluate if the condition is zero. */
    private final Expression falseBranch;

    /**
     * Constructs a Conditional node with the specified condition and branches.
     *
     * @param condition the condition expression to evaluate
     * @param trueBranch the expression to evaluate if the condition is non-zero
     * @param falseBranch the expression to evaluate if the condition is zero
     */
    public Conditional(Expression condition, Expression trueBranch, Expression falseBranch) {
        assert condition != null && trueBranch != null && falseBranch != null;
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    /**
     * Evaluates this Conditional by evaluating the condition and selecting the appropriate branch.
     *
     * @param vars the variable table for any variable dependencies
     * @return the result of evaluating either the true or false branch
     * @throws UnboundVariableException if a variable in this conditional is unbound
     */
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        double condValue = condition.eval(vars);
        return condValue != 0.0 ? trueBranch.eval(vars) : falseBranch.eval(vars);
    }

    /**
     * Returns the number of operations required to evaluate this Conditional, which includes the
     * condition and the more expensive branch, plus one for the conditional check itself.
     */
    @Override
    public int opCount() {
        return 1 + condition.opCount() + Math.max(trueBranch.opCount(), falseBranch.opCount());
    }

    /**
     * Returns the infix notation string for this Conditional, with subexpressions separated by ? and :.
     */
    @Override
    public String infixString() {
        return "(" + condition.infixString() + " ? " + trueBranch.infixString() + " : " + falseBranch.infixString() + ")";
    }

    /**
     * Returns the postfix notation string for this Conditional.
     */
    @Override
    public String postfixString() {
        return condition.postfixString() + " " + trueBranch.postfixString() + " " + falseBranch.postfixString() + " ?:";
    }

    /**
     * Optimizes this Conditional by optimizing the condition and both branches.
     * If the condition can be evaluated to a constant, returns the appropriate branch directly.
     */
    @Override
    public Expression optimize(VarTable vars) {
        assert vars != null;

        Expression condOpt = condition.optimize(vars);
        Expression trueOpt = trueBranch.optimize(vars);
        Expression falseOpt = falseBranch.optimize(vars);

        // If condition can be fully evaluated to a constant, choose the appropriate branch
        if (condOpt instanceof Constant) {
            try {
                double condValue = condOpt.eval(vars);
                return condValue != 0.0 ? trueOpt : falseOpt;
            } catch (UnboundVariableException e) {
                // Unexpected, should not throw if condOpt is a Constant
                throw new RuntimeException(e);
            }
        }
        return new Conditional(condOpt, trueOpt, falseOpt);
    }

    /**
     * Checks equality between this Conditional and another object. Two Conditional nodes are equal
     * if their condition, true branch, and false branch expressions are all equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Conditional conditional = (Conditional) other;
        return condition.equals(conditional.condition) &&
                trueBranch.equals(conditional.trueBranch) &&
                falseBranch.equals(conditional.falseBranch);
    }
    @Override
    public int hashCode() {
        return Objects.hash(condition, trueBranch, falseBranch);
    }

    @Override
    public String toString() {
        return "Conditional(condition=" + condition + ", trueBranch=" + trueBranch + ", falseBranch=" + falseBranch + ")";
    }

    /**
     * Returns a set containing all variable names that this conditional expression depends on.
     *
     * @return A Set of variable names representing all dependencies of this conditional expression,
     *         including dependencies of the condition, true branch, and false branch.
     */
    @Override
    public Set<String> dependencies() {
        Set<String> deps = new HashSet<>(condition.dependencies());
        deps.addAll(trueBranch.dependencies());
        deps.addAll(falseBranch.dependencies());
        return deps;
    }

}
