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

    record ExprStmt(Expr child) implements Stmt {
        @Override
        public void exec(RunState state) {
            child.eval(state);
        }
    }

    record Assign(String name, Expr child) implements Stmt {
        @Override
        public void exec(RunState state) {
            Value val = child.eval(state);
            state.currentFrame().assign(name, val);
        }
    }

    record Print(Expr child) implements Stmt {
        @Override
        public void exec(RunState state) {
            System.out.println(child.eval(state).str());
        }
    }

    record IfElse(Expr condition, Stmt ifBody, Stmt elseBody) implements Stmt {
        @Override
        public void exec(RunState state) {
            if (condition.eval(state).bool()) {
                ifBody.exec(state);
            }
            else if (elseBody != null) {
                elseBody.exec(state);
            }
        }
    }

    record While(Expr condition, Stmt body) implements Stmt {
        @Override
        public void exec(RunState state) {
            while (condition.eval(state).bool()) {
                body.exec(state);
            }
        }
    }
}
