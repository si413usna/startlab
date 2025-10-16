package si413;

import java.util.List;

/** AST nodes for statements.
 * Statements can be executed but do not return a value.
 */
public interface Stmt {
    /** Executes this AST node.
     * The Interpreter instance interp is needed to pass around
     * any shared state of the interpreter, such as a symbol table.
     */
    void exec(Interpreter interp);

    // ******** AST Node types for statements ******** //

    record Block(List<Stmt> children) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            for (Stmt child : children) {
                child.exec(interp);
            }
        }
    }

    record ExprStmt(Expr<?> child) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            child.eval(interp);
        }
    }

    record AssignString(String name, Expr<String> child) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            String val = child.eval(interp);
            interp.getStringVars().put(name, val);
        }
    }

    record AssignBool(String name, Expr<Boolean> child) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            boolean val = child.eval(interp);
            interp.getBoolVars().put(name, val);
        }
    }

    record PrintString(Expr<String> child) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            System.out.println(child.eval(interp));
        }
    }

    record PrintBool(Expr<Boolean> child) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            if (child.eval(interp)) System.out.println("True");
            else System.out.println("False");
        }
    }

    record IfElse(Expr<Boolean> condition, Stmt ifBody, Stmt elseBody) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            if (condition.eval(interp)) {
                ifBody.exec(interp);
            }
            else {
                elseBody.exec(interp);
            }
        }
    }

    record While(Expr<Boolean> condition, Stmt body) implements Stmt {
        @Override
        public void exec(Interpreter interp) {
            while (condition.eval(interp)) {
                body.exec(interp);
            }
        }
    }
}
