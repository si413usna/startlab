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

    /** Compiles this AST node and returns the register name containing the result.
     * The default method just throws an error to remind you to fill in the code.
     */
    default String compile(CompileState cstate) {
        return Errors.error(String.format("missing compile() method in %s class in Stmt.java",
            getClass().getSimpleName()));
    }

    // ******* AST node types for expressions that return a String ******** //

    record StringLit(String value) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            return value;
        }

        @Override
        public String compile(CompileState cstate) {
            return cstate.addStringLit(value);
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

        @Override
        public String compile(CompileState cstate) {
            String lreg = lhs.compile(cstate);
            String rreg = rhs.compile(cstate);
            String res = cstate.freshReg();
            cstate.out().format("  %s = call ptr @concat_strings(ptr %s, ptr %s)\n", res, lreg, rreg);
            return res;
        }
    }

    record Reverse(Expr<String> child) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            String childVal = child.eval(state);
            return new StringBuilder(childVal).reverse().toString();
        }

        @Override
        public String compile(CompileState cstate) {
            String creg = child.compile(cstate);
            String res = cstate.freshReg();
            cstate.out().format("  %s = call ptr @reverse_string(ptr %s)\n", res, creg);
            return res;
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

        @Override
        public String compile(CompileState cstate) {
            String lreg = lhs.compile(cstate);
            String rreg = rhs.compile(cstate);
            String res = cstate.freshReg();
            cstate.out().format("  %s = call ptr @chop_string(ptr %s, ptr %s)\n", res, lreg, rreg);
            return res;
        }
    }

    record BoolToString(Expr<Boolean> child) implements Expr<String> {
        @Override
        public String eval(RunState state) {
            return child.eval(state) ? "yes" : "no";
        }

        @Override
        public String compile(CompileState cstate) {
            String creg = child.compile(cstate);
            String res = cstate.freshReg();
            cstate.out().format("  %s = call ptr @bool_to_string(i1 %s)\n", res, creg);
            return res;
        }
    }

    record Input() implements Expr<String> {
        @Override
        public String eval(RunState state) {
            return state.stdin().nextLine();
        }

        @Override
        public String compile(CompileState cstate) {
            String res = cstate.freshReg();
            cstate.out().format("  %s = call ptr @read_line()\n", res);
            return res;
        }
    }

    // ******* AST node types for expressions that return a Boolean ******** //

    record BoolLit(Boolean value) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            return value;
        }

        @Override
        public String compile(CompileState cstate) {
            return value ? "1" : "0";
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

        @Override
        public String compile(CompileState cstate) {
            String lreg = lhs.compile(cstate);
            String rreg = rhs.compile(cstate);
            String res = cstate.freshReg();
            cstate.out().format("  %s = call i1 @string_less(ptr %s, ptr %s)\n", res, lreg, rreg);
            return res;
        }
    }

    record Or(Expr<Boolean> lhs, Expr<Boolean> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            return lhs.eval(state) || rhs.eval(state);
        }

        @Override
        public String compile(CompileState cstate) {
            String lreg = lhs.compile(cstate);
            String rreg = rhs.compile(cstate);
            String res = cstate.freshReg();
            cstate.out().format("  %s = or i1 %s, %s\n", res, lreg, rreg);
            return res;
        }
    }

    record Not(Expr<Boolean> child) implements Expr<Boolean> {
        @Override
        public Boolean eval(RunState state) {
            return !child.eval(state);
        }

        @Override
        public String compile(CompileState cstate) {
            String creg = child.compile(cstate);
            String res = cstate.freshReg();
            // note, LLVM has no "not" command in 3AC, instead we xor the value with 1
            cstate.out().format("  %s = xor i1 %s, 1\n", res, creg);
            return res;
        }
    }
}
