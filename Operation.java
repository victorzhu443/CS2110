// Operation.java
package cs2110;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a binary arithmetic operation on two operand expressions.
 */
public class Operation implements Expression {

    /** The left operand expression. */
    private final Expression left;

    /** The right operand expression. */
    private final Expression right;

    /** The operator for this operation (e.g., +, -, *, /, ^). */
    private final Operator operator;

    /**
     * Constructs an Operation node with the specified operands and operator.
     *
     * @param operator the operator to apply
     * @param left the left operand expression
     * @param right the right operand expression
     */
    public Operation(Operator operator, Expression left, Expression right) {
        assert operator != null && left != null && right != null;
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    /**
     * Evaluates this Operation by evaluating both operands and applying the operator.
     *
     * @param vars the variable table for any variable dependencies
     * @return the result of applying the operator on the evaluated operands
     * @throws UnboundVariableException if a variable in this operation is unbound
     */
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        double leftValue = left.eval(vars);
        double rightValue = right.eval(vars);
        return operator.operate(leftValue, rightValue);
    }

    /**
     * Returns the number of operations needed to evaluate this Operation, which includes both
     * operands and the operation itself.
     */
    @Override
    public int opCount() {
        return 1 + left.opCount() + right.opCount();
    }

    /**
     * Returns the infix notation string for this Operation, enclosing it in parentheses.
     */
    @Override
    public String infixString() {
        return "(" + left.infixString() + " " + operator.symbol() + " " + right.infixString() + ")";
    }

    /**
     * Returns the postfix notation string for this Operation.
     */
    @Override
    public String postfixString() {
        return left.postfixString() + " " + right.postfixString() + " " + operator.symbol();
    }

    /**
     * Returns the set of variable dependencies for this Operation, which is the union of the
     * dependencies in the left and right operands.
     */
    @Override
    public Set<String> dependencies() {
        Set<String> dependencies = new HashSet<>(left.dependencies());
        dependencies.addAll(right.dependencies());
        return dependencies;
    }

    /**
     * Optimizes this Operation by attempting to replace constant-only sub-expressions with their
     * evaluated results.
     */
    @Override
    public Expression optimize(VarTable vars) {
        assert vars != null;
        Expression leftOpt = left.optimize(vars);
        Expression rightOpt = right.optimize(vars);

        if (leftOpt instanceof Constant && rightOpt instanceof Constant) {
            return new Constant(operator.operate(((Constant) leftOpt).eval(vars), ((Constant) rightOpt).eval(vars)));
        }
        return new Operation(operator, leftOpt, rightOpt);
    }

    /**
     * Checks equality between this Operation and another object. Two Operation nodes are equal if
     * their operator, left operand, and right operand are all equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Operation operation = (Operation) other;
        return operator.equals(operation.operator) &&
                left.equals(operation.left) &&
                right.equals(operation.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }

    @Override
    public String toString() {
        return "Operation(operator=" + operator.symbol() + ", left=" + left + ", right=" + right + ")";
    }
}
