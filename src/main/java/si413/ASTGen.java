package si413;

import java.util.List;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTree;

/** This class is used to create the AST from a parse tree.
 */
public class ASTGen {
    private BlockVisitor blockVis = new BlockVisitor();
    private StmtVisitor stmtVis = new StmtVisitor();
    private StringExprVisitor sexpVis = new StringExprVisitor();
    private BoolExprVisitor bexpVis = new BoolExprVisitor();

    /** Turns a parse tree Prog node into a complete AST.
     * This is called by main()
     */
    public static Stmt gen(ParseTree parseTree) {
        return new ASTGen().blockVis.visit(parseTree);
    }


    // YOU NEED TO FILL IN THE VISIT METHODS!

    class BlockVisitor extends Visitor<Stmt.Block> {
        // TODO these are just dummy methods; replace them!

        public Stmt.Block visitHelloProg(Grammar.HelloProgContext ctx) {
            Stmt helloStmt = new Stmt.Print(new Expr.StringLit("hello"));
            Stmt.Block prog = visit(ctx.prog());
            prog.children().add(0, helloStmt);
            return prog;
        }

        public Stmt.Block visitEmptyProg(Grammar.EmptyProgContext ctx) {
            return new Stmt.Block(new ArrayList<>());
        }
    }

    class StmtVisitor extends Visitor<Stmt> {
        // TODO
    }

    class StringExprVisitor extends Visitor<Expr<String>> {
        // TODO
    }

    class BoolExprVisitor extends Visitor<Expr<Boolean>> {
        // TODO
    }
}
