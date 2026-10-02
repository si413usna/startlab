package si413;

import java.io.PrintWriter;
import org.antlr.v4.runtime.tree.ParseTree;

public class Compiler {
    private StatementVisitor stmtVis = new StatementVisitor();
    private ExpressionVisitor exprVis = new ExpressionVisitor();
    private PrintWriter out = null;

    /** Compiles the given parse tree to the given output stream. */
    public void compile(ParseTree ptree, PrintWriter out) {
        this.out = out;
        out.println("define i32 @main() {");
        stmtVis.visit(ptree);
        out.println("  ret i32 0");
        out.println("}");
        // TODO you will have to add more here, e.g. for string literals
    }

    /** Visitor methods for statements that don't return any value.  */
    private class StatementVisitor extends Visitor<Void> {
        @Override
        public Void visitEmptyProg(Grammar.EmptyProgContext ctx) {
            // TODO this is just a dummy method - replace with all
            // of the relevant visit methods for your own grammar!
            out.println("  ; this goes inside of the LLVM main()");
            return null;
        }
    }

    /** Visitor methods for expressions that return a value.  */
    private class ExpressionVisitor extends Visitor<String> {
    }
}
