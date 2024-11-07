import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for writing and evaluating formulas
 * @author Aleksandr Stinchcomb
 * @version 1.0
 */
public class Formula implements Evaluator {
    // -- Constructors -- \\
    /** The object to hold the formula string */
    private final StringBuffer FORMULA = new StringBuffer();
    /** A dictionary containing the values of the variables within the formula */
    private final MapBuffer<String, Object> VALUES = new MapBuffer<>();
    /** A {@link Set} of all the variables the formula has. */
    private final Set<String> VARIABLES = Collections.synchronizedSet(new HashSet<>());

    /** A read-only copy of the {@code VALUES} dictionary that also reflects changes made to {@code VALUES} */
    private final Map<String, Object> READONLY_VALUES = Collections.unmodifiableMap(VALUES);
    /** A read-only copy of the {@code VARIABLES} set that also reflects changes made to {@code VARIABLES} */
    private final Set<String> READONLY_VARIABLES = Collections.unmodifiableSet(VARIABLES);

    // -- Constructors -- \\
    /**Creates an empty {@link Formula} object*/
    public Formula() {}
    /**
     * Creates a {@link Formula} object with a pre-defined formula
     * @param seq the formula to create the object from
     */
    public Formula(CharSequence seq) {
        add(seq);
    }

    // -- Methods -- \\
    /**
     * Checks if {@code ch} is part of the alphabet.
     * @param ch the character to check.
     * @return {@code true} if {@code ch} is alphabetic.
     */
    private static boolean isAlphabetic(char ch) {
        return (ch>0x60 && ch<0x7B) || (ch>=0x40 && ch<0x5B);
    }
    /**
     * Checks if {@code seq} only contains alphabetic characters.
     * @param seq the {@link CharSequence} to check.
     * @return {@code true} if {@code seq} is completely alphabetic.
     */
    private static boolean isAlphabetic(CharSequence seq) {
        return seq.toString().matches("[a-zA-Z]+");
    }

    /**
     * Updates the {@code variables} array and the {@code VALUES} map to reflect the
     * changes to the formula. They will only contain variables the formula contains.
     */
    private void update() {
        { //remake the variables array
            String[] variables = FORMULA.toString().split("(?<=[a-zA-Z])[^a-zA-Z]+(?=[a-zA-Z])"); //split the string at all non-alphabet characters, except at the ends
            final int LENGTH = variables.length;

            //removes any non-alphabet characters in the first and last elements of the array
            if (LENGTH > 0) variables[0] = variables[0].replaceAll("[^a-zA-Z]+", "");
            if (LENGTH > 1) variables[LENGTH-1] = variables[LENGTH-1].replaceAll("[^a-zA-Z]+", "");

            VARIABLES.clear();
            Collections.addAll(VARIABLES, variables);
        }
        { //redefine the variables dictionary
            List<String> variables = new ArrayList<>(VARIABLES);

            for (String variable : VALUES.keySet()) { //remove the variable if it is no longer in the algorithm
                if (!variables.contains(variable)) VALUES.remove(variable);
                variables.remove(variable); //remove variable from the list
            }
            for (String variable : variables) { //put all the new variables in the dictionary
                VALUES.put(variable, null);
            }
        }
    }

    /**
     * Checks if {@code seq} is a variable. A variable is defined as a sequence of alphabetic
     * characters with capitalization occurring at the beginning of the sequence.
     * @param seq the {@link CharSequence} to check.
     * @return {@code true} if {@code seq} is a variable.
     */
    public static boolean isVariable(CharSequence seq) {
        return seq.toString().matches("^[A-Z]*[a-z]*");
    }

    @Override
    public void set(CharSequence seq) {
        if (seq.toString().contentEquals(FORMULA)) return; //ignore the call if there's no difference between the formula and seq
        FORMULA.replace(0, FORMULA.length(), seq.toString());
        update();
    }

    @Override
    public void clear() {
        if (FORMULA.isEmpty()) return; //ignore the call if the formula contains no characters
        FORMULA.delete(0, FORMULA.length());
        update();
    }

    @Override
    public void add(char ch) {
        FORMULA.append(ch);
        update();
    }
    @Override
    public void add(CharSequence seq) {
        FORMULA.append(seq);
        update();
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void add(int index, char ch) {
        FORMULA.insert(index, ch);
        update();
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void add(int index, CharSequence seq) {
        FORMULA.insert(index, seq);
        update();
    }

    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void remove() {
        FORMULA.deleteCharAt(FORMULA.length()-1);
        update();
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void remove(int index) {
        FORMULA.deleteCharAt(index);
        update();
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void remove(int start, int end) {
        FORMULA.delete(start, end);
        update();
    }

    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void replace(int index, char ch) {
        FORMULA.replace(index, index+1, ""+ch);
        update();
    }
    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public void replace(int start, int end, CharSequence seq) {
        FORMULA.replace(start, end, seq.toString());
        update();
    }

    @Override
    public int length() {
        return FORMULA.toString().length();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     */
    @Override
    public char charAt(int index) {
        return FORMULA.charAt(index);
    }

    /** @throws StringIndexOutOfBoundsException {@inheritDoc} */
    @Override
    public CharSequence subSequence(int start, int end) {
        return FORMULA.subSequence(start, end);
    }

    @Override
    public String toString() {
        return FORMULA.toString();
    }

    /**
     * Gets a {@code Map} of the variables and their respective values. The {@code Map} is
     * read-only and reflects changes to the formula, so it only contains existing variables.
     * @return a read-only {@link Map} with the variables as the keys
     */
    public Map<String, Object> values() {
        return READONLY_VALUES;
    }

    /**
     * Gets a {@code Set} containing the variables the formula has. The {@code Set} is
     * read-only and reflects changes to the formula, so it only contains existing variables.
     * @return a {@link Set} containing the variables the formula has.
     */
    public Set<String> variables() {
        return READONLY_VARIABLES;
    }

    /**
     * Checks if the formula contains the variable {@code variable}.
     * @param variable the variable to check for
     * @return {@code true} if the formula contains {@code variable}
     * @see #isVariable(CharSequence)
     */
    public boolean containsVariable(String variable) {
        if (isVariable(variable)) return VARIABLES.contains(variable);
        return false;
    }

    /**
     * Gets the value of a variable
     * @param variable the name of the variable
     * @return the value of the variable ({@code null} if it hasn't been defined)
     * @throws UnknownVariableException if {@code variable} is not in the formula
     * @see #containsVariable(String)
     */
    public Object getValue(String variable) {
        if (!containsVariable(variable)) throw new UnknownVariableException(String.format("Variable '%s' was not found in the formula", variable));
        return VALUES.get(variable);
    }

    /**
     * Sets the value of a variable
     * @param variable the name of the variable
     * @param value the value to set the variable to
     * @return This object.
     * @throws UnknownVariableException if {@code variable} is not contained in {@code variables()}.
     */
    public Formula defineVariable(String variable, Object value) {
        if (!containsVariable(variable)) throw new UnknownVariableException(String.format("Variable '%s' was not found in the formula", variable));
        VALUES.put(variable, value);
        return this;
    }

    /**
     * Replaces the formula variables with their respective values to create an expression
     * @return an {@link Expression} containing the variable values along with any non-defined variables
     */
    public Expression expression() {
        final Expression EXPRESSION = new Expression(FORMULA);

        int start = 0;
        boolean selecting = false;
        final StringBuilder SELECTION = new StringBuilder();

        for (int index=start; index<EXPRESSION.length(); index++) {
            char character = EXPRESSION.charAt(index);

            if (!selecting && isAlphabetic(character)) {
                selecting = true;
                start = index;
                SELECTION.append(character);
            } else if (selecting) {
                if (!isVariable(SELECTION.append(character))) {
                    selecting = false;
                    SELECTION.setLength(0);

                    String variable = EXPRESSION.subSequence(start, index).toString();
                    if (containsVariable(variable)) {
                        String value = VALUES.get(variable).toString();
                        EXPRESSION.replace(start, index, value);
                        index = start + value.length();
                    }
                }
            }
        }
        return EXPRESSION;
    }
}