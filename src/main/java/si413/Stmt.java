package si413;

import java.util.List;

/** AST nodes for statements.
 * Statements can be executed but do not return a value.
 */
public interface Stmt {
    /** Executes this AST node.
     * The Interpreter instance state is needed to pass around
     * any shared state of the interpreter, such as a symbol table.
     */
    void exec(RunState state);

    // ******** AST Node types for statements ******** //

    record Block(List<Stmt> children) implements Stmt {
        @Override
        public void exec(RunState state) {
            for (Stmt child : children) {
                child.exec(state);
            }
        }
    }

    record ExprStmt(Expr<?> child) implements Stmt {
        @Override
        public void exec(RunState state) {
            child.eval(state);
        }
    }

    record AssignString(String name, Expr<String> child) implements Stmt {
        @Override
        public void exec(RunState state) {
            String val = child.eval(state);
            state.strVars().put(name, val);
        }
    }

    record AssignBool(String name, Expr<Boolean> child) implements Stmt {
        @Override
        public void exec(RunState state) {
            boolean val = child.eval(state);
            state.boolVars().put(name, val);
        }
    }

    record Print(Expr<String> child) implements Stmt {
        @Override
        public void exec(RunState state) {
            System.out.println(child.eval(state));
        }
    }

    record IfElse(Expr<Boolean> condition, Stmt ifBody, Stmt elseBody) implements Stmt {
        @Override
        public void exec(RunState state) {
            if (condition.eval(state)) {
                ifBody.exec(state);
            }
            else if (elseBody != null) {
                elseBody.exec(state);
            }
        }
    }

    record While(Expr<Boolean> condition, Stmt body) implements Stmt {
        @Override
        public void exec(RunState state) {
            while (condition.eval(state)) {
                body.exec(state);
            }
        }
    }
}
