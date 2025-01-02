package cs2110;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Utilities for parsing RPN expressions.
 */
public class RpnParser {

    /**
     * Parse the RPN expression in `exprString` and return the corresponding expression tree. Tokens
     * must be separated by whitespace.  Valid tokens include decimal numbers (scientific notation
     * allowed), arithmetic operators (+, -, *, /, ^), the conditional operator (?:), function names
     * (with the suffix "()"), and variable names (anything else).  When a function name is
     * encountered, the corresponding function will be retrieved from `funcDefs` using the name
     * (without "()" suffix) as the key.
     *
     * @throws IncompleteRpnException     if the expression has too few or too many operands
     *                                    relative to operators and functions.
     * @throws UndefinedFunctionException if a function name applied in `exprString` is not present
     *                                    in `funcDefs`.
     */
    public static Expression parse(String exprString, Map<String, UnaryFunction> funcDefs)
            throws IncompleteRpnException, UndefinedFunctionException {
        assert exprString != null;
        assert funcDefs != null;

        // Each token will result in a subexpression being pushed onto this stack.  If the
        // subexpression requires arguments, they are first popped off of this stack.
        Deque<Expression> stack = new ArrayDeque<>();

        // Loop over each token in the expression string from left to right
        for (Token token : Token.tokenizer(exprString)) {
            // TODO: Based on the dynamic type of the token, create the appropriate Expression node
            //  and push it onto the stack, popping arguments as needed.
            //  The "number" token is done for you as an example.
            if (token instanceof Token.Number) {
                Token.Number numToken = (Token.Number) token;
                stack.push(new Constant(numToken.doubleValue()));
            } else if (token instanceof Token.Variable) {
                Token.Variable varToken = (Token.Variable) token;
                stack.push(new Variable(varToken.value()));
            } else if (token instanceof Token.Operator) {
                if (stack.size() < 2) throw new IncompleteRpnException(exprString, stack.size());
                Expression right = stack.pop();
                Expression left = stack.pop();
                stack.push(new Operation(((Token.Operator) token).opValue(), left, right));
            } else if (token instanceof Token.CondOp) {
                if (stack.size() < 3) throw new IncompleteRpnException(exprString, stack.size());
                Expression falseBranch = stack.pop();
                Expression trueBranch = stack.pop();
                Expression condition = stack.pop();
                stack.push(new Conditional(condition, trueBranch, falseBranch));
            } else if (token instanceof Token.Function) {
                String funcName = ((Token.Function) token).name(); // Use name() instead of value()
                UnaryFunction function = funcDefs.get(funcName);
                if (function == null) throw new UndefinedFunctionException(funcName);
                if (stack.isEmpty()) throw new IncompleteRpnException(exprString, stack.size());
                Expression argument = stack.pop();
                stack.push(new Application(function, argument));
            }
        }

        // TODO: Return the overall expression node.  (This might also be a good place to check that
        //  the string really did correspond to a single expression.)
        if (stack.size() != 1) throw new IncompleteRpnException(exprString, stack.size());
        return stack.pop();
    }
}
