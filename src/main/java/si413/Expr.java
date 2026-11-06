package si413;

/** AST nodes for expressions.
 * Expressions can be evaluated and return a Value.
 */
public interface Expr {
    /** Evaluates this AST node and returns the result.
     * The RunState instance state is needed to pass around
     * any shared state of the interpreter, such as a symbol table.
     */
    Value eval(RunState state);

    record Literal(Value val) implements Expr {
        @Override
        public Value eval(RunState state) {
            return val;
        }
    }

    record Var(String name) implements Expr {
        @Override
        public Value eval(RunState state) {
            Value val = state.currentFrame().lookup(name);
            if (val == null)
                return Errors.error(String.format("undefined variable %s", name));
            else return val;
        }
    }

    record Concat(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(RunState state) {
            String lval = lhs.eval(state).str();
            String rval = rhs.eval(state).str();
            return new Value.Str(lval + rval);
        }
    }

    record Reverse(Expr child) implements Expr {
        @Override
        public Value eval(RunState state) {
            String childVal = child.eval(state).str();
            String reversed = new StringBuilder(childVal).reverse().toString();
            return new Value.Str(reversed);
        }
    }

    record Chop(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(RunState state) {
            String lval = lhs.eval(state).str();
            String rval = rhs.eval(state).str();
            int found = lval.indexOf(rval);
            if (found < 0) found = lval.length();
            return new Value.Str(lval.substring(0, found));
        }
    }

    record BoolToString(Expr child) implements Expr {
        @Override
        public Value eval(RunState state) {
            return new Value.Str(child.eval(state).bool() ? "yes" : "no");
        }
    }

    record Input() implements Expr {
        @Override
        public Value eval(RunState state) {
            return new Value.Str(state.readLine());
        }
    }

    record StrLess(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(RunState state) {
            String lval = lhs.eval(state).str();
            String rval = rhs.eval(state).str();
            return new Value.Bool(lval.compareTo(rval) < 0);
        }
    }

    record Or(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(RunState state) {
            // short-circuit evaluation: if lhs is true, don't bother evaluating rhs
            if (lhs.eval(state).bool()) return new Value.Bool(true);
            else return new Value.Bool(rhs.eval(state).bool());
        }
    }

    record Not(Expr child) implements Expr {
        @Override
        public Value eval(RunState state) {
            return new Value.Bool(!child.eval(state).bool());
        }
    }
}
