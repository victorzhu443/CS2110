package cs2110;

import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

/**
 * A VarTable implemented using a Map from the Java Collections Framework.
 */
public class MapVarTable implements VarTable {

    Map<String, Double> map;

    /**
     * Create an empty MapVarTable.
     */
    public MapVarTable() {
        // Using a SortedMap results in consistent ordering, which is convenient for testing.
        map = new TreeMap<>();
    }

    /**
     * Create an empty MapVarTable.
     */
    public static MapVarTable empty() {
        return new MapVarTable();
    }

    /**
     * Create a MapVarTable associating `value1` with variable `name1`.
     */
    public static MapVarTable of(String name1, double value1) {
        assert name1 != null;

        MapVarTable ans = new MapVarTable();
        ans.set(name1, value1);
        return ans;
    }

    /**
     * Create a MapVarTable associating `value1` with variable `name1` and `value2` with `name2`.
     */
    public static MapVarTable of(String name1, double value1, String name2, double value2) {
        assert name1 != null;
        assert name2 != null;

        MapVarTable ans = new MapVarTable();
        ans.set(name1, value1);
        ans.set(name2, value2);
        return ans;
    }

    @Override
    public double get(String name) throws UnboundVariableException {
        assert name != null;

        Double value = map.get(name);
        if (value == null) {
            throw new UnboundVariableException(name);
        }
        return value;
    }

    @Override
    public void set(String name, double value) {
        assert name != null;

        map.put(name, value);
    }

    @Override
    public void unset(String name) {
        assert name != null;

        map.remove(name);
    }

    @Override
    public boolean contains(String name) {
        assert name != null;

        return map.containsKey(name);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<String> names() {
        return map.keySet();
    }
}
