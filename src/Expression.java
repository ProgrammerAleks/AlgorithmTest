import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * A class that can evaluate expressions to find the result. Expressions are
 * @author Aleksandr Stinchcomb
 * @version 1.0
 * @see Evaluator
 */
public class Expression implements Evaluator {
    // -- Attributes -- \\
    private static final String NUMBERS = "0123456789."; //The supported numbers
    private static final String PEMDAS = "(e^*/%+#-"; //The order of operations
    //TODO: add an interpreted language for expressions to read and use
    //TODO: add vector support through the use of chevrons
    //TODO: add support for different number bases
    private static final String[] BRACKETS = {"()", "[]", "{}", "<>"}; //The list of bracket types that are supported in string expressions
    //Mathematics Bracket Usage; (): Order of Operation, []: Matrix, {}: Set, <>: Vector
    //Bracket Types: Parentheses, Square Brackets, Braces, and Chevrons

    public static final String[] OPERATIONS = {"^","*","/","%","+","-","e"}; //The list of math operations supported in string expressions
    public static final String[] FUNCTIONS = {"sqrt","abs","sin","cos","tan","arcsin","arccos","arctan","!"}; //The list of math functions supported in string expressions

    private static final String[] UNIT_FUNCTIONS = {"sin","cos","tan","arcsin","arccos","arctan"}; //The list of functions that require input values to be converted

    private final StringBuffer EXPRESSION = new StringBuffer();

    // -- Constructors -- \\
    /**Creates an empty {@link Expression} object*/
    public Expression() {}
    /**
     * Creates an {@link Expression} object with a pre-defined expression
     * @param seq the expression to be evaluated
     */
    public Expression(CharSequence seq) {
        EXPRESSION.append(seq);
    }

    // -- Methods -- \\
    private boolean isOperator(int ch) {
        return (PEMDAS.indexOf(ch) > -1);
    }

    private boolean isNumber(int ch) {
        return (NUMBERS.indexOf(ch) > -1);
    }

    private boolean isLetter(int ch) {
        return Character.isLetter(ch);
    }

    private boolean isFunction(String str) {
        for (String function : FUNCTIONS) {
            if (str.equals(function)) {return true;}
        }
        return false;
    }

    private boolean needsConversion(String function) {
        for (String func : UNIT_FUNCTIONS) {
            if (function.equals(func)) {return true;}
        }
        return false;
    }

    private boolean canStack(int stackOperator, int operator) {
        int stackIndex = PEMDAS.indexOf(stackOperator)+1;
        int operatorIndex = PEMDAS.indexOf(operator)+1;

        int stackGroup = (int)Math.ceil(stackIndex/3f);
        int operatorGroup = (int)Math.ceil(operatorIndex/3f);

        return stackOperator == '(' || (stackGroup > 1 && stackGroup == operatorGroup) ? false : stackIndex >= operatorIndex;
    }

    private BigDecimal function(Stack<String> stack, String function) throws NumberFormatException, ParseException, ArithmeticException {
        BigDecimal number = new BigDecimal(stack.pop());
        double value = number.doubleValue();

        switch (function) {
            case "sqrt":
            	return number.sqrt(MathContext.DECIMAL128);
            case "abs":
            	return number.abs(MathContext.DECIMAL128);
            case "sin":
            	return new BigDecimal(Math.sin(value), MathContext.DECIMAL128);
            case "cos":
            	return new BigDecimal(Math.cos(value), MathContext.DECIMAL128);
            case "tan":
            	return new BigDecimal(Math.tan(value), MathContext.DECIMAL128);
            case "arcsin":
            	return new BigDecimal(Math.asin(value), MathContext.DECIMAL128);
            case "arccos":
            	return new BigDecimal(Math.acos(value), MathContext.DECIMAL128);
            case "arctan":
            	return new BigDecimal(Math.atan(value), MathContext.DECIMAL128);
            case "!":
	            if (Utility.isDecimal(number)) {
	                return new BigDecimal(Utility.gamma(number.doubleValue()));
	            } else {
	                return new BigDecimal(Utility.fact(number.longValue()));
	            }
        }
        throw new ParseException(String.format("Unknown function '%s'", function), 0);
    }

    private BigDecimal operation(Stack<String> stack, char operator) throws NumberFormatException, ParseException, ArithmeticException {
        String secondary = stack.pop();
        String primary = stack.pop();
        
        BigDecimal first = new BigDecimal(primary);
        BigDecimal second = new BigDecimal(secondary);

        switch (operator) {
            case 'e':
            	return first.scaleByPowerOfTen(second.intValue());
            case '^':
            	return new BigDecimal(Math.pow(first.doubleValue(), second.doubleValue()));
            case '*':
            	return first.multiply(second, MathContext.DECIMAL128); //prime.doubleValue() * secondary.doubleValue();
            case '/':
            	if (second.equals("0")) {throw new ArithmeticException("Divide by 0");}
            	return first.divide(second, MathContext.DECIMAL128); //prime.doubleValue() / secondary.doubleValue();
            case '%':
            	if (second.equals("0")) {throw new ArithmeticException("Divide by 0");}
            	return first.remainder(second, MathContext.DECIMAL128); //prime.doubleValue() % secondary.doubleValue();
            case '+':
            	return first.add(second, MathContext.DECIMAL128); //prime.doubleValue() + secondary.doubleValue();
            case '-':
            	return first.subtract(second, MathContext.DECIMAL128); //prime.doubleValue() - secondary.doubleValue();
        }
        throw new ParseException(String.format("Unknown operator '%c'", operator), 0);
    }

    private BigDecimal execute(Stack<String> stack, boolean isOperator, String action) throws NumberFormatException, ParseException, ArithmeticException {
        return isOperator ? operation(stack, action.charAt(0)) : function(stack, action);
    }

    /**
     * Converts an expression from infix to postfix
     * @return a {@link Queue} with the values, operators, and functions of the expression ordered
     * to be processed
     * @throws Exception if the {@code expression} is {@code null} or has invalid syntax
     */
    private Queue<String> toPostfix() throws Exception {
        if (EXPRESSION.toString().length() <= 0) {throw new Exception("Null expression");}

        final String EXPRESSION_FORMAT = EXPRESSION.toString().replaceAll("\\s", "")+" ";
        char[] chars = EXPRESSION_FORMAT.toCharArray();

        Stack<String> stack = new Stack<>();
        Queue<String> queue = new LinkedList<>();

        int numberStart = -1;
        int functionStart = -1;
        boolean expectOperand = true;

        for (int index = 0; index < chars.length; index++) {
            char character = chars[index];
            
            if (isLetter(character) && ((character != 'e' && functionStart >= -1) || (character == 'e' && functionStart > -1))) {
                if (functionStart == -1) {functionStart = index;}
                continue;
            } else if (functionStart > -1) {
                stack.push(EXPRESSION_FORMAT.substring(functionStart, index));
                functionStart = -1;
                expectOperand = true;
            }

            if (isNumber(character)) {
                if (numberStart == -1) {numberStart = index;}
                continue;
            } else if (numberStart > -1) {
                if ((index - numberStart) == 1 && chars[numberStart] == '.') {
                    throw new Exception("Syntax");
                } else {
                    queue.add(EXPRESSION_FORMAT.substring(numberStart, index));
                }
                numberStart = -1;
                expectOperand = false;
            }

            if (character == '!') {
                queue.add(""+character);
                expectOperand = false;
                continue;
            }
            
            if (character == ')') {
                if (stack.empty() || !stack.contains("(")) {
                    throw new Exception("Syntax");
                } else {
                    while (!stack.empty() && !stack.peek().equals("(")) {
                        queue.add(stack.pop());
                    }
                    stack.pop();
                }
            } else if (isOperator(character)) {
                if (character == '-' && expectOperand) {
                    stack.push("#");
                    expectOperand = false;
                } else if (character == '(' || stack.empty() || canStack(stack.peek().charAt(0), character)) {
                    if (!expectOperand && character == '(') {stack.push("*");}

                    expectOperand = !(character == '-' && (expectOperand || (!stack.empty() && stack.peek().equals("-"))));
                    stack.push(""+ (expectOperand ? character : '#'));

                    // TODO: Add support for fractions parsing
                } else {
                    expectOperand = true;
                    
                    if (!stack.empty() && !stack.peek().equals("(")) {queue.add(stack.pop());}
                    stack.push(""+character);
                }
            }
        }
        while (!stack.empty()) { //add any remaining items on the stack to the queue
            queue.add(stack.pop());
        }

        return queue;
    }

    /**
     * Processes a postfix expression and finds the result
     * @param queue the postfix expression
     * @return the result of the postfix expression
     * @throws Exception if an error occurs while processing
     */
    private BigDecimal processPostfix(Queue<String> queue) throws Exception {
        Stack<String> stack = new Stack<>();
        
        while (!queue.isEmpty()) {
            String element = queue.remove();
            boolean isOperator = isOperator(element.charAt(0));

            if (isNumber(element.charAt(0))) {
                boolean negative = !queue.isEmpty() && queue.peek().equals("#");
                if (negative) {queue.remove();}

                stack.push(negative ? "-"+element : element);
            } else if (isFunction(element) || isOperator) {
                if (element == "#") {
                    if (!stack.empty()) {
                        stack.push((stack.peek().charAt(0) == '-') ? stack.pop().substring(1) : "-"+stack.pop());
                        continue;
                    } else {
                        throw new Exception("Syntax");
                    }
                } else if (stack.empty() || (isOperator && stack.size() < 2)) {
                    throw new Exception("Syntax");
                }

                stack.push(execute(stack, isOperator, element).toString());
            } else {
                throw new Exception("Syntax");
            }
        }

        if (stack.size() > 1 || stack.empty()) {throw new Exception("Syntax");}
        return new BigDecimal(stack.pop());
    }

    @Override
    public void set(CharSequence seq) {
        if (seq.toString().contentEquals(EXPRESSION)) return; //ignore the call if there's no difference between the expression and seq
        EXPRESSION.replace(0, EXPRESSION.length(), seq.toString());
    }

    @Override
    public void clear() {
        if (EXPRESSION.isEmpty()) return; //ignore the call if the expression contains no characters
        EXPRESSION.delete(0, EXPRESSION.length());
    }

    @Override
    public void add(char ch) {
        EXPRESSION.append(ch);
    }
    @Override
    public void add(CharSequence seq) {
        EXPRESSION.append(seq);
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void add(int index, char ch) {
        EXPRESSION.insert(index, ch);
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void add(int index, CharSequence seq) {
        EXPRESSION.insert(index, seq);
    }

    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void remove() {
        EXPRESSION.deleteCharAt(EXPRESSION.length()-1);
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void remove(int index) {
        EXPRESSION.deleteCharAt(index);
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void remove(int start, int end) {
        EXPRESSION.delete(start, end);
    }

    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void replace(int index, char ch) {
        EXPRESSION.replace(index, index+1, ""+ch);
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void replace(int start, int end, CharSequence seq) {
        EXPRESSION.replace(start, end, seq.toString());
    }

    @Override
    public int length() {
        return EXPRESSION.toString().length();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     */
    @Override
    public char charAt(int index) {
        return EXPRESSION.charAt(index);
    }

    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public CharSequence subSequence(int start, int end) {
        return EXPRESSION.subSequence(start, end);
    }

    @Override
    public String toString() {
        return EXPRESSION.toString();
    }

    /**
     * Evaluates the expression with the given processing settings
     * @return the result of the evaluated expression
     * @throws Exception if the expression is incorrectly formatted
     */
    public BigDecimal evaluate() throws Exception {
        Queue<String> postfix = toPostfix();
        return processPostfix(postfix);
    }
}