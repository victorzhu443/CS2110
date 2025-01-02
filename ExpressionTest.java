package cs2110;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantExpressionTest {

    @Test
    @DisplayName("A Constant node should evaluate to its value (regardless of var table)")
    void testEval() throws UnboundVariableException {
        Expression expr = new Constant(1.5);
        assertEquals(1.5, expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("A Constant node should report that 0 operations are required to evaluate it")
    void testOpCount() {
        Expression expr = new Constant(1.5);
        assertEquals(0, expr.opCount());
    }


    @Test
    @DisplayName("A Constant node should produce an infix representation with just its value (as " +
            "formatted by String.valueOf(double))")
    void testInfix() {
        Expression expr = new Constant(1.5);
        assertEquals("1.5", expr.infixString());

        expr = new Constant(Math.PI);
        assertEquals("3.141592653589793", expr.infixString());
    }

    @Test
    @DisplayName("A Constant node should produce an postfix representation with just its value " +
            "(as formatted by String.valueOf(double))")
    void testPostfix() {
        Expression expr = new Constant(1.5);
        assertEquals("1.5", expr.postfixString());

        expr = new Constant(Math.PI);
        assertEquals("3.141592653589793", expr.postfixString());
    }


    @Test
    @DisplayName("A Constant node should equal itself")
    void testEqualsSelf() {
        Expression expr = new Constant(1.5);
        // Normally `assertEquals()` is preferred, but since we are specifically testing the
        // `equals()` method, we use the more awkward `assertTrue()` to make that call explicit.
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("A Constant node should equal another Constant node with the same value")
    void testEqualsTrue() {
        Expression expr1 = new Constant(1.5);
        Expression expr2 = new Constant(1.5);
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Constant node should not equal another Constant node with a different value")
    void testEqualsFalse() {
        Expression expr1 = new Constant(1.5);
        Expression expr2 = new Constant(2.0);
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName("A Constant node does not depend on any variables")
    void testDependencies() {
        Expression expr = new Constant(1.5);
        Set<String> deps = expr.dependencies();
        assertTrue(deps.isEmpty());
    }


    @Test
    @DisplayName("A Constant node should optimize to itself (regardless of var table)")
    void testOptimize() {
        Expression expr = new Constant(1.5);
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(expr, opt);
    }
}

class VariableExpressionTest {

    @Test
    @DisplayName("A Variable node should evaluate to its variable's value when that variable is " +
            "in the var map")
    void testEvalBound() throws UnboundVariableException {
        Expression expr = new Variable("x"); //Complete
        assertEquals(1.5, expr.eval(MapVarTable.of("x", 1.5)));
    }

    @Test
    @DisplayName("A Variable node should throw an UnboundVariableException when evaluated if its " +
            "variable is not in the var map")
    void testEvalUnbound() {
        // TODO: Uncomment these lines when you have read about testing exceptions in the handout.
        // They assume that your `Variable` constructor takes its name as an argument.
         Expression expr = new Variable("x");
         assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("A Variable node should report that 0 operations are required to evaluate it")
    void testOpCount() {
        Expression expr = new Variable("x"); //Complete
        assertEquals(0, expr.opCount());
    }


    @Test
    @DisplayName("A Variable node should produce an infix representation with just its name")
    void testInfix() {
        Expression expr = new Variable("x"); //Complete
        assertEquals("x", expr.infixString());
    }

    @Test
    @DisplayName("A Variable node should produce an postfix representation with just its name")
    void testPostfix() {
        Expression expr = new Variable("x"); //Complete
        assertEquals("x", expr.postfixString());
    }


    @Test
    @DisplayName("A Variable node should equal itself")
    void testEqualsSelf() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Variable("x");
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("A Variable node should equal another Variable node with the same name")
    void testEqualsTrue() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        // Force construction of new String objects to detect inadvertent use of `==`
        Expression expr1 = new Variable(new String("x"));
        Expression expr2 = new Variable(new String("x"));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Variable node should not equal another Variable node with a different name")
    void testEqualsFalse() {
        Expression expr1 = new Variable("x"); //Complete
        Expression expr2 = new Variable("y");
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName("A Variable node only depends on its name")
    void testDependencies() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Variable("x");
        Set<String> deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertEquals(1, deps.size());
    }


    @Test
    @DisplayName("A Variable node should optimize to a Constant if its variable is in the var map")
    void testOptimizeBound() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Variable("x");
        Expression opt = expr.optimize(MapVarTable.of("x", 1.5));
        assertEquals(new Constant(1.5), opt);
    }

    @Test
    @DisplayName("A Variable node should optimize to itself if its variable is not in the var map")
    void testOptimizeUnbound() {
        Expression expr = new Variable("x"); //Complete
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(expr, opt);
    }
}

class OperationExpressionTest {

    @Test
    @DisplayName("An Operation node for ADD with two Constant operands should evaluate to their " +
            "sum")
    void testEvalAdd() throws UnboundVariableException {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Constant(2));
        assertEquals(3.5, expr.eval(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Operation node for ADD with a Variable for an operand should evaluate " +
            "to its operands' sum when the variable is in the var map")
    void testEvalAddBound() throws UnboundVariableException {  //Complete
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Constant(2));
        assertEquals(3.5, expr.eval(MapVarTable.of("x", 1.5)));
    }

    @Test
    @DisplayName("An Operation node for ADD with a Variable for an operand should throw an " +
            "UnboundVariableException when evaluated if the variable is not in the var map")
    void testEvalAddUnbound() { //Complete
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Constant(2));
        assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("An Operation node with leaf operands should report that 1 operation is " +
            "required to evaluate it")
    void testOpCountLeaves() {
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Constant(2));
        assertEquals(1, expr.opCount()); //Complete
    }


    @Test
    @DisplayName("An Operation node with an Operation for either or both operands should report " +
            "the correct number of operations to evaluate it")
    void testOpCountRecursive() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Operation(Operator.ADD,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals(2, expr.opCount());

        expr = new Operation(Operator.SUBTRACT,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Operation(Operator.DIVIDE, new Constant(1.5), new Variable("x")));
        assertEquals(3, expr.opCount());
    }


    @Test
    @DisplayName("An Operation node with leaf operands should produce an infix representation " +
            "consisting of its first operand, its operator symbol surrounded by spaces, and " +
            "its second operand, all enclosed in parentheses")
    void testInfixLeaves() { //Complete
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Constant(2));
        assertEquals("(1.5 + 2.0)", expr.infixString());
    }

    @Test
    @DisplayName("An Operation node with an Operation for either operand should produce the " +
            "expected infix representation with parentheses around each operation")
    void testInfixRecursive() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Operation(Operator.ADD,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals("((1.5 * x) + 2.0)", expr.infixString());

        expr = new Operation(Operator.SUBTRACT,
                new Constant(2.0),
                new Operation(Operator.DIVIDE, new Constant(1.5), new Variable("x")));
        assertEquals("(2.0 - (1.5 / x))", expr.infixString());
    }


    @Test
    @DisplayName("An Operation node with leaf operands should produce a postfix representation " +
            "consisting of its first operand, its second operand, and its operator symbol " +
            "separated by spaces")
    void testPostfixLeaves() { //Complete
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Constant(2));
        assertEquals("1.5 2.0 +", expr.postfixString());
    }

    @Test
    @DisplayName("An Operation node with an Operation for either operand should produce the " +
            "expected postfix representation")
    void testPostfixRecursive() { // Complete
        Expression expr = new Operation(Operator.ADD,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals("1.5 x * 2.0 +", expr.postfixString());

        expr = new Operation(Operator.SUBTRACT,
                new Constant(2.0),
                new Operation(Operator.DIVIDE, new Constant(1.5), new Variable("x")));
        assertEquals("2.0 1.5 x / -", expr.postfixString());
    }


    @Test
    @DisplayName("An Operation node should equal itself")
    void testEqualsSelf() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("An Operation node should equal another Operation node with the same " +
            "operator and operands")
    void testEqualsTrue() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr1 = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        Expression expr2 = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("An Operation node should not equal another Operation node with a different " +
            "operator")
    void testEqualsFalse() {
        Expression expr1 = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        Expression expr2 = new Operation(Operator.SUBTRACT, new Constant(1.5), new Variable("x"));
        assertFalse(expr1.equals(expr2)); //Complete
    }


    @Test
    @DisplayName("An Operation node depends on the dependencies of both of its operands")
    void testDependencies() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Variable("y"));
        Set<String> deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertTrue(deps.contains("y"));
        assertEquals(2, deps.size());
    }


    @Test
    @DisplayName("An Operation node for ADD with two Constant operands should optimize to a " +
            "Constant containing their sum")
    void testOptimizeAdd() { //Complete
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Constant(2.0));
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(new Constant(3.5), opt);
    }


}

@Nested
class ConditionalExpressionTest {

    @Test
    @DisplayName(
            "A Conditional node with Constant non-zero condition and Constant branches should "
                    + "evaluate to the first branch's value.")
    void testEvalConstTrue() throws UnboundVariableException {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Conditional(new Constant(1), new Constant(2), new Constant(5));
        assertEquals(2.0, expr.eval(MapVarTable.empty()));
    }

    @Test
    @DisplayName("A Conditional node with Constant zero condition and Constant branches should "
            + "evaluate to the second branch's value.")
    void testEvalConstFalse() throws UnboundVariableException { //Complete
        Expression expr = new Conditional(new Constant(0), new Constant(2), new Constant(5));
        assertEquals(5.0, expr.eval(MapVarTable.empty()));
    }

    @Test
    @DisplayName(
            "A Conditional node with a Variable condition should evaluate to the appropriate "
                    + "branch when the variable has a zero or non-zero value in the var map")
    void testEvalCondBound() throws UnboundVariableException {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Conditional(new Variable("x"), new Constant(2), new Constant(5));
        assertEquals(2.0, expr.eval(MapVarTable.of("x", 1.0)));
        assertEquals(5.0, expr.eval(MapVarTable.of("x", 0.0)));
    }

    @Test
    @DisplayName("A Conditional node with a Variable condition should throw an "
            + "UnboundVariableException when evaluated if the variable is not in the var map")
    void testEvalCondUnbound() { //Complete
        Expression expr = new Conditional(new Variable("x"), new Constant(2), new Constant(5));
        assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("A Conditional node with leaf condition and branches should report that 1 "
            + "operation is required to evaluate it")
    void testOpCountLeaves() { //Complete
        Expression expr = new Conditional(new Constant(1), new Constant(2), new Constant(5));
        assertEquals(1, expr.opCount());
    }

    @Test
    @DisplayName(
            "A Conditional node with non-leaf condition and branches with different op counts "
                    + "should report the correct number of operations to evaluate it regardless of which "
                    + "branch is more expensive")
    void testOpCountRecursive() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        // True branch is more expensive
        Expression expr1 = new Conditional(
                new Operation(Operator.ADD, new Variable("x"), new Constant(3)),
                new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")),
                new Constant(7));
        assertEquals(3, expr1.opCount());

        // False branch is more expensive
        Expression expr2 = new Conditional(
                new Operation(Operator.SUBTRACT, new Variable("x"), new Constant(1)),
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                expr1);
        assertEquals(5, expr2.opCount());
    }



    @Test
    @DisplayName("A Conditional node with leaf condition and branches should produce an infix "
            + "representation consisting of its condition, the '?' symbol surrounded by spaces, "
            + "its true branch, the ':' symbol surrounded by spaces, and its false branch, all "
            + "enclosed in parentheses")
    void testInfixLeaves() { //COmplete
        Expression expr = new Conditional(new Constant(1), new Constant(2), new Constant(5));
        assertEquals("(1.0 ? 2.0 : 5.0)", expr.infixString());
    }

    @Test
    @DisplayName(
            "A Conditional node with Operation condition and branches should produce the " +
                    "expected infix representation.")
    void testInfixRecursive() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Conditional(
                new Operation(Operator.ADD, new Variable("x"), new Constant(3)),
                new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")),
                new Constant(7));
        assertEquals("((x + 3.0) ? (2.0 * y) : 7.0)", expr.infixString());
    }


    @Test
    @DisplayName("A Condition node with leaf condition and branches should produce a postfix "
            + "representation consisting of its condition, its true branch, its false branch, and "
            + "the '?:' symbol, separated by spaces")
    void testPostfixLeaves() { //COmplete
        Expression expr = new Conditional(new Constant(1), new Constant(2), new Constant(5));
        assertEquals("1.0 2.0 5.0 ?:", expr.postfixString());
    }

    @Test
    @DisplayName("A Conditional node with Operation condition and branches should produce the "
            + "expected postfix representation")
    void testPostfixRecursive() { //Complete
        Expression expr = new Conditional(
                new Operation(Operator.ADD, new Variable("x"), new Constant(3)),
                new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")),
                new Constant(7));
        assertEquals("x 3.0 + 2.0 y * 7.0 ?:", expr.postfixString());
    }


    @Test
    @DisplayName("A Condition node should equal itself")
    void testEqualsSelf() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Conditional(new Variable("x"), new Constant(2), new Constant(5));
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("A Condition node should equal another Condition node with the same " +
            "condition and branches")
    void testEqualsTrue() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr1 = new Conditional(
                new Operation(Operator.ADD, new Variable("x"), new Constant(3)),
                new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")),
                new Constant(7));
        Expression expr2 = new Conditional(
                new Operation(Operator.ADD, new Variable("x"), new Constant(3)),
                new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")),
                new Constant(7));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Condition node should not equal another Condition node with a different " +
            "condition")
    void testEqualsFalseCondition() { //Complete
        Expression expr1 = new Conditional(new Variable("x"), new Constant(2), new Constant(5));
        Expression expr2 = new Conditional(new Variable("y"), new Constant(2), new Constant(5));
        assertFalse(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Condition node should not equal another Condition node with a different " +
            "branch")
    void testEqualsFalseBranch() { //Complete
        Expression expr1 = new Conditional(new Variable("x"), new Constant(2), new Constant(5));
        Expression expr2 = new Conditional(new Variable("x"), new Constant(3), new Constant(5));
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName(
            "A Condition node depends on the dependencies of its condition and both of its "
                    + "branches")
    void testDependencies() {
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        Expression expr = new Conditional(
                new Operation(Operator.ADD, new Variable("x"), new Constant(3)),
                new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")),
                new Variable("z"));
        Set<String> deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertTrue(deps.contains("y"));
        assertTrue(deps.contains("z"));
        assertEquals(3, deps.size());
    }


    @Test
    @DisplayName(
            "A Conditional node with Constant condition should optimize to its appropriate "
                    + "optimized branch")
    void testOptimizeConstCondition() { //Complete
        Expression expr = new Conditional(new Constant(1), new Constant(2), new Constant(5));
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(new Constant(2), opt);
    }

    @Test
    @DisplayName("A Conditional node with a non-leaf branch and an Operation condition that "
            + "optimizes to a constant should optimize to its appropriate optimized branch")
    void testOptimizeExprCondition() { //Complete
        Expression expr = new Conditional(
                new Operation(Operator.ADD, new Constant(1), new Constant(2)),
                new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")),
                new Constant(7));
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(new Operation(Operator.MULTIPLY, new Constant(2), new Variable("y")), opt);
    }
}

