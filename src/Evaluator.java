/**
 * A template for classes that evaluate expressions.
 * @author Aleksandr Stinchcomb
 * @version 1.0
 */
abstract interface Evaluator extends CharSequence {
    //TODO: make interface an abstract class
    /**
     * Sets the text of the {@link Evaluator} to {@code seq}
     * @param seq the {@link CharSequence} to set the text to
     */
    public abstract void set(CharSequence seq);

    /** Removes all text from the {@link Evaluator} */
    public abstract void clear();

    /**
     * Adds {@code ch} to the end
     * @param ch the {@link Character} to add to the end of the expression
     */
    public abstract void add(char ch);
    /**
     * Adds {@code seq} to the end
     * @param seq the {@link CharSequence} to add to the end of the expression
     */
    public abstract void add(CharSequence seq);
    /**
     * Inserts {@code ch} at {@code index}
     * @param index the index to insert {@code ch} at
     * @param ch the {@link Character} to insert at the {@code index}
     * @throws StringIndexOutOfBoundsException if the {@code index} argument is negative or greater than {@code length()}.
     */
    public abstract void add(int index, char ch);
    /**
     * Inserts {@code seq} at {@code index}
     * @param index the index to insert {@code seq} at
     * @param seq the {@link CharSequence} to insert at {@code index}
     * @throws StringIndexOutOfBoundsException if the {@code index} argument is negative or greater than {@code length()}.
     */
    public abstract void add(int index, CharSequence seq);

    /**
     * Deletes the character at the end of the {@code expression}
     * @throws StringIndexOutOfBoundsException if the {@code length()} is {@code 0}.
     */
    public abstract void remove();
    /**
     * Deletes the character at {@code index} in {@code expression}
     * @param index the index of the character to delete
     * @throws StringIndexOutOfBoundsException if the {@code index} is negative or greater than or equal to {@code length()}.
     */
    public abstract void remove(int index);
    /**
     * Deletes the selection from {@code start} to {@code end} in {@code expression}
     * @param start the start index for the selection in {@code expression}
     * @param end the end index for the selection in {@code expression}
     * @throws StringIndexOutOfBoundsException if {@code start} is negative, greater than {@code length()}, or greater than {@code end}.
     */
    public abstract void remove(int start, int end);

    /**
     * Replaces the character at {@code index} with {@code ch}
     * @param index the index of the character to replace
     * @param ch the {@link Character} to replace the selection with
     * @throws StringIndexOutOfBoundsException if {@code index} is negative or greater than {@code length()}}.
     */
    public abstract void replace(int index, char ch);
    /**
     * Replaces the selection from {@code start} to {@code end} with {@code seq}
     * @param start the start index for the selection in {@code expression}
     * @param end the end index for the selection in {@code expression}
     * @param seq the {@link CharSequence} to replace the selection with
     * @throws StringIndexOutOfBoundsException if {@code start} is negative, greater than {@code length()}, or greater than {@code end}.
     */
    public abstract void replace(int start, int end, CharSequence seq);

    /**
     * @throws StringIndexOutOfBoundsException if the {@code index} argument is negative or not less than {@code length()}.
     * @see        #length()
     */
    @Override
    public abstract char charAt(int index);

    /** @throws StringIndexOutOfBoundsException if {@code start} or {@code end} are negative, if {@code end} is greater than {@code length()}, or if {@code start} is greater than {@code end}. */
    @Override
    public abstract CharSequence subSequence(int start, int end);
}
