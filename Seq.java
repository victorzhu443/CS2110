package cs2110;

/**
 * A mutable list of elements of type `T`.  Null elements are not allowed.
 */
public interface Seq<T> extends Iterable<T> {

    /**
     * Return the number of elements in this list.
     */
    int size();

    /**
     * Return whether this list contains an element equal to `elem` (according to `equals()`).
     * Requires `elem` is not null.
     */
    boolean contains(T elem);

    /**
     * Return the element at index `index` in this list.  The index of the first element is 0.
     * Requires {@code 0 <= index < size()}.  Will not return null.
     */
    T get(int index);

    /**
     * Insert element `elem` at the beginning of this list. Example: if the list is [8, 7, 4],
     * prepend(2) would change the list to [2, 8, 7, 4].  Requires `elem` is not null.
     */
    void prepend(T elem);

    /**
     * Add element `elem` to the end of this list. Example: if the list is [8, 7, 4], append(2)
     * would change the list to [8, 7, 4, 2].  Requires `elem` is not null.
     */
    void append(T elem);

    /**
     * Insert element `elem` into the list just before the first occurrence of element `successor`.
     * Requires that `successor` is contained in the list and that `elem` and `successor` are not
     * null.
     * <p>
     * Example: If the list is [3, 8, 2], then insertBefore(1, 8) would change the list to [3, 1, 8,
     * 2].
     */
    void insertBefore(T elem, T successor);

    /**
     * Remove the first occurrence of element `elem` (if any) from this list.  Return whether the
     * list changed.  Requires `elem` is not null.
     */
    boolean remove(T elem);
}
