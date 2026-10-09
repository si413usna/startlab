package si413;

/** AST nodes for expressions.
 * Expressions can be evaluated and return a value.
 * In the interface the return type is the generic type T.
 * Actual implementations of the interface should specify
 * either String or Boolean for T.
 */
public interface Expr<T> {
    /** Evaluates this AST node and returns the result.
     * The RunState instance state is needed to pass around
     * any shared state of the interpreter, such as a symbol table.
     */
    T eval(RunState state);

    // ******* AST node types for expressions that return a String ******** //

    record StringLit(String value) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            return value;
        }
    }

    record StrVar(String name) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            String val = state.strVars().get(name);
            if (val == null)
                return Errors.error(String.format("undefined string variable %s", name));
            else return val;
        }
    }

    record Concat(Expr<String> lhs, Expr<String> rhs) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            String lval = lhs.eval(state);
            String rval = rhs.eval(state);
            return lval + rval;
        }
    }

    record Reverse(Expr<String> child) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            String childVal = child.eval(state);
            return new StringBuilder(childVal).reverse().toString();
        }
    }

    record Chop(Expr<String> lhs, Expr<String> rhs) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            String lval = lhs.eval(state);
            String rval = rhs.eval(state);
            int found = lval.indexOf(rval);
            if (found < 0) return lval;
            else return lval.substring(0, found);
        }
    }

    record BoolToString(Expr<Boolean> child) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            return child.eval(state) ? "yes" : "no";
        }
    }

    record Input() implements Expr<String> {
        @Override
        public String eval(RunState state) {
            return state.stdin().nextLine();
        }
    }

    // ******* AST node types for expressions that return a Boolean ******** //

    record BoolLit(Boolean value) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            return value;
        }
    }

    record BoolVar(String name) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            Boolean val = state.boolVars().get(name);
            if (val == null)
                return Errors.error(String.format("undefined bool variable %s", name));
            else return val;
        }
    }

    record StrLess(Expr<String> lhs, Expr<String> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            String lval = lhs.eval(state);
            String rval = rhs.eval(state);
            return lval.compareTo(rval) < 0;
        }
    }

    record Or(Expr<Boolean> lhs, Expr<Boolean> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            return lhs.eval(state) || rhs.eval(state);
        }
    }

    record Not(Expr<Boolean> child) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            return !child.eval(state);
        }
    }
}
