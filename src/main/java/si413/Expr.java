package si413;

/** AST nodes for expressions.
 * Expressions can be evaluated and return a value.
 * In the interface the return type is the generic type T.
 * Actual implementations of the interface should specify
 * either String or Boolean for T.
 */
public interface Expr<T> {
    /** Evaluates this AST node and returns the result. */
    T eval(Interpreter interp);

    /** Compiles this AST node and returns the register holding the result. */
    String compile(Compiler comp);

    // ******* AST node types for expressions that return a String ******** //

    record StringLit(String value) implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            return value;
        }

        @Override
        public String compile(Compiler comp) {
            return comp.addStringLit(value);
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

        @Override
        public String compile(Compiler comp) {
            throw new UnsupportedOperationException("delete this exception and implement this method!"); // TODO
        }
    }

    record Concat(Expr<String> lhs, Expr<String> rhs) implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            String lval = lhs.eval(interp);
            String rval = rhs.eval(interp);
            return lval + rval;
        }

        @Override
        public String compile(Compiler comp) {
            String lreg = lhs.compile(comp);
            String rreg = rhs.compile(comp);
            String res = comp.nextRegister();
            comp.dest().format("  %s = call ptr @concat_strings(ptr %s, ptr %s)\n", res, lreg, rreg);
            return res;
        }
    }

    record Reverse(Expr<String> child) implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            String childVal = child.eval(interp);
            return new StringBuilder(childVal).reverse().toString();
        }

        @Override
        public String compile(Compiler comp) {
            String chreg = child.compile(comp);
            String res = comp.nextRegister();
            comp.dest().format("  %s = call ptr @reverse_string(ptr %s)\n", res, chreg);
            return res;
        }
    }

    record Input() implements Expr<String> {
        @Override
        public String eval(Interpreter interp) {
            return interp.readInputLine();
        }

        @Override
        public String compile(Compiler comp) {
            String res = comp.nextRegister();
            comp.dest().format("  %s = call ptr @read_line()\n", res);
            return res;
        }
    }

    // ******* AST node types for expressions that return a Boolean ******** //

    record BoolLit(Boolean value) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return value;
        }

        @Override
        public String compile(Compiler comp) {
            String res = comp.nextRegister();
            if (value) comp.dest().format("  %s = or i1 true, true\n", res);
            else comp.dest().format("  %s = and i1 false, false\n", res);
            return res;
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

        @Override
        public String compile(Compiler comp) {
            throw new UnsupportedOperationException("delete this exception and implement this method!"); // TODO
        }
    }

    record StrLess(Expr<String> lhs, Expr<String> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            String lval = lhs.eval(interp);
            String rval = rhs.eval(interp);
            return lval.compareTo(rval) < 0;
        }

        @Override
        public String compile(Compiler comp) {
            String lreg = lhs.compile(comp);
            String rreg = rhs.compile(comp);
            String res = comp.nextRegister();
            comp.dest().format("  %s = call i1 @string_less(ptr %s, ptr %s)\n", res, lreg, rreg);
            return res;
        }
    }

    record Contains(Expr<String> lhs, Expr<String> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            String lval = lhs.eval(interp);
            String rval = rhs.eval(interp);
            return lval.contains(rval);
        }

        @Override
        public String compile(Compiler comp) {
            String lreg = lhs.compile(comp);
            String rreg = rhs.compile(comp);
            String res = comp.nextRegister();
            comp.dest().format("  %s = call i1 @string_contains(ptr %s, ptr %s)\n", res, lreg, rreg);
            return res;
        }
    }

    record And(Expr<Boolean> lhs, Expr<Boolean> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return lhs.eval(interp) && rhs.eval(interp);
        }

        @Override
        public String compile(Compiler comp) {
            String lreg = lhs.compile(comp);
            String rreg = rhs.compile(comp);
            String res = comp.nextRegister();
            comp.dest().format("  %s = and i1 %s, %s\n", res, lreg, rreg);
            return res;
        }
    }

    record Or(Expr<Boolean> lhs, Expr<Boolean> rhs) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return lhs.eval(interp) || rhs.eval(interp);
        }

        @Override
        public String compile(Compiler comp) {
            String lreg = lhs.compile(comp);
            String rreg = rhs.compile(comp);
            String res = comp.nextRegister();
            comp.dest().format("  %s = or i1 %s, %s\n", res, lreg, rreg);
            return res;
        }
    }

    record Not(Expr<Boolean> child) implements Expr<Boolean> {
        @Override
        public Boolean eval(Interpreter interp) {
            return !child.eval(interp);
        }

        @Override
        public String compile(Compiler comp) {
            String chreg = child.compile(comp);
            String res = comp.nextRegister();
            comp.dest().format("  %s = xor i1 %s, true\n", res, chreg);
            return res;
        }
    }
}
