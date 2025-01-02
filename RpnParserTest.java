package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RpnParserTest {

    @Test
    @DisplayName("Parsing an expression consisting of a single number should yield a Constant " +
            "node with that value")
    void testParseConstant() throws IncompleteRpnException, UndefinedFunctionException {
        Expression expr = RpnParser.parse("1.5", Map.of());
        assertEquals(new Constant(1.5), expr);
    }

    @Test
    @DisplayName("Parsing an expression consisting of a single identifier should yield a " +
            "Variable node with that name")
    void testParseVariable() throws IncompleteRpnException, UndefinedFunctionException {
        Expression expr = RpnParser.parse("x", Map.of());
        // TODO: Uncomment this test, adjusting constructor invocations as necessary
        assertEquals(new Variable("x"), expr);
    }


    @Test
    @DisplayName("Parsing an expression ending with an operator should yield an Operation node " +
            "evaluating to the expected value")
    void testParseOperation()
            throws UnboundVariableException, IncompleteRpnException, UndefinedFunctionException {
        // Simple addition (commutative)
        Expression expr = RpnParser.parse("1 1 +", Map.of());
        assertInstanceOf(Operation.class, expr);
        assertEquals(2.0, expr.eval(MapVarTable.empty()));

        // Non-commutative operation with subtraction
        Expression exprSub = RpnParser.parse("5 3 -", Map.of());
        assertInstanceOf(Operation.class, exprSub);
        assertEquals(2.0, exprSub.eval(MapVarTable.empty()));

        // Non-commutative operation with division
        Expression exprDiv = RpnParser.parse("8 4 /", Map.of());
        assertInstanceOf(Operation.class, exprDiv);
        assertEquals(2.0, exprDiv.eval(MapVarTable.empty()));

        // Mixed operation (addition and multiplication) with recursive evaluation
        Expression exprMixed = RpnParser.parse("2 3 + 4 *", Map.of());
        assertInstanceOf(Operation.class, exprMixed);
        assertEquals(20.0, exprMixed.eval(MapVarTable.empty())); // (2 + 3) * 4 = 20

        // Further nested expression: (2 + 3) * (4 - 1)
        Expression exprNested = RpnParser.parse("2 3 + 4 1 - *", Map.of());
        assertInstanceOf(Operation.class, exprNested);
        assertEquals(15.0, exprNested.eval(MapVarTable.empty())); // (2 + 3) * (4 - 1) = 15
    }


    @Test
    @DisplayName("Parsing an expression ending with a function should yield an Application node " +
            "evaluating to the expected value")
    void testParseApplication()
            throws UnboundVariableException, IncompleteRpnException, UndefinedFunctionException {
        Expression expr = RpnParser.parse("4 sqrt()", UnaryFunction.mathDefs());
        assertInstanceOf(Application.class, expr);
        assertEquals(2.0, expr.eval(MapVarTable.empty()));

    }


    @Test
    @DisplayName("Parsing an expression ending with the conditional symbol should yield a "
            + "Conditional node evaluating to the expected value")
    void testParseConditional()
            throws UnboundVariableException, IncompleteRpnException, UndefinedFunctionException {
        Expression expr = RpnParser.parse("1 2 3 ?:", UnaryFunction.mathDefs());
        // TODO: Uncomment this test
        assertInstanceOf(Conditional.class, expr);
        assertEquals(2.0, expr.eval(MapVarTable.empty()));

    }

    @Test
    @DisplayName("Parsing an expression with an undefined function should throw UndefinedFunctionException")
    void testParseUndefinedFunction() {
        assertThrows(UndefinedFunctionException.class, () -> RpnParser.parse("1 foo()", Map.of()));
    }


    @Test
    @DisplayName("Parsing an empty expression should throw an IncompleteRpnException")
    void testParseEmpty() {
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("", Map.of()));
    }

    @Test
    @DisplayName("Parsing an expression that leave more than one term on the stack should throw " +
            "an IncompleteRpnException")
    void testParseIncomplete() {
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("1 1 1 +", Map.of()));
    }

    @Test
    @DisplayName("Parsing an expression that consumes more terms than are on the stack should " +
            "throw an IncompleteRpnException")
    void testParseUnderflow() {
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("1 1 + +", Map.of()));
        //Added test
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("1 +", Map.of()));
    }

    @Test
    @DisplayName("Parsing an expression that applies an unknown function should throw an " +
            "UnknownFunctionException")
    void testParseUndefined() {
        assertThrows(UndefinedFunctionException.class, () -> RpnParser.parse("1 foo()", Map.of()));
    }


}
