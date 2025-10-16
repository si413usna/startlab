package si413;

/** AST nodes for expressions.
 * Expressions can be evaluated and return a value.
 * In the interface the return type is the generic type T.
 * Actual implementations of the interface should specify
 * either String or Boolean for T.
 */
public interface Expr<T> {
    /** Evaluates this AST node and returns the result.
     * The Interpreter instance interp is needed to pass around
     * any shared state of the interpreter, such as a symbol table.
     */
    T eval(Interpreter interp);

    // ******* AST node types for expressions that return a String ******** //

    record StringLit(String value) implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            return value;
        }
    }

    record StrVar(String name) implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            String val = interp.getStringVars().get(name);
            if (val == null)
                return Errors.error(String.format("undefined string variable %s", name));
            else return val;
        }
    }

    record Concat(Expr<String> lhs, Expr<String> rhs) implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            String lval = lhs.eval(interp);
            String rval = rhs.eval(interp);
            return lval + rval;
        }
    }

    record Reverse(Expr<String> child) implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            String childVal = child.eval(interp);
            return new StringBuilder(childVal).reverse().toString();
        }
    }

    record Input() implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            return interp.readInputLine();
        }
    }

    // ******* AST node types for expressions that return a Boolean ******** //

    record BoolLit(Boolean value) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return value;
        }
    }

    record BoolVar(String name) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            Boolean val = interp.getBoolVars().get(name);
            if (val == null)
                return Errors.error(String.format("undefined bool variable %s", name));
            else return val;
        }
    }

    record StrLess(Expr<String> lhs, Expr<String> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            String lval = lhs.eval(interp);
            String rval = rhs.eval(interp);
            return lval.compareTo(rval) < 0;
        }
    }

    record Contains(Expr<String> lhs, Expr<String> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            String lval = lhs.eval(interp);
            String rval = rhs.eval(interp);
            return lval.contains(rval);
        }
    }

    record And(Expr<Boolean> lhs, Expr<Boolean> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return lhs.eval(interp) && rhs.eval(interp);
        }
    }

    record Or(Expr<Boolean> lhs, Expr<Boolean> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return lhs.eval(interp) || rhs.eval(interp);
        }
    }

    record Not(Expr<Boolean> child) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return !child.eval(interp);
        }
    }
}
