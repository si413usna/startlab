package si413;

/** Holds a generic Value that can be either a string or a boolean.
 * To make a string, create an instance of Value.Str("like this").
 * To make a bool, create an instance of Value.Bool(true||false)
 * To convert a Value object to an actual String or bool, call str() or bool().
 */
public interface Value {
    default String str() {
        return Errors.error(String.format("Value type error: Expected string, got %s", toString()));
    }

    default boolean bool() {
        return Errors.error(String.format("Value type error: Expected boolean, got %s", toString()));
    }

    record Str(String value) implements Value {
        @Override
        public String str() { return value; }

        @Override
        public String toString() { return value; }
    }

    record Bool(Boolean value) implements Value {
        @Override
        public boolean bool() { return value; }

        @Override
        public String toString() {
            return value ? "True" : "False";
        }
    }
}
