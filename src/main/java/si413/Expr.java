package si413;

import java.util.List;
import java.util.ArrayList;

/** AST nodes for expressions.
 * Expressions can be evaluated and return a value.
 */
public interface Expr {
    /** Evaluates this AST node and returns the result. */
    Value eval(Interpreter interp);

    record StringLit(String value) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            return new Value.Str(value);
        }
    }

    record Var(String name) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            return interp.getEnv().lookup(name);
        }
    }

    record Concat(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            String lval = lhs.eval(interp).str();
            String rval = rhs.eval(interp).str();
            return new Value.Str(lval + rval);
        }
    }

    record Reverse(Expr child) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            String childVal = child.eval(interp).str();
            return new Value.Str(new StringBuilder(childVal).reverse().toString());
        }
    }

    record Input() implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            return new Value.Str(interp.readInputLine());
        }
    }

    record BInput(String trueString) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            return new Value.Bool(interp.readInputLine().equals(trueString));
        }
    }

    // ******* AST node types for expressions that return a Boolean ******** //

    record BoolLit(Boolean value) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            return new Value.Bool(value);
        }
    }

    record StrLess(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            String lval = lhs.eval(interp).str();
            String rval = rhs.eval(interp).str();
            return new Value.Bool(lval.compareTo(rval) < 0);
        }
    }

    record Contains(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            String lval = lhs.eval(interp).str();
            String rval = rhs.eval(interp).str();
            return new Value.Bool(lval.contains(rval));
        }
    }

    record And(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            Value left = lhs.eval(interp);
            if (left.bool()) return rhs.eval(interp);
            else return left;
        }
    }

    record Or(Expr lhs, Expr rhs) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            Value left = lhs.eval(interp);
            if (left.bool()) return left;
            else return rhs.eval(interp);
        }
    }

    record Not(Expr child) implements Expr {
        @Override
        public Value eval(Interpreter interp) {
            return new Value.Bool(!child.eval(interp).bool());
        }
    }
}
