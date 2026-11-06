package si413;

import java.util.LinkedList;
import org.antlr.v4.runtime.tree.ParseTree;

/** This class is used to create the AST from a parse tree.
 */
public class ASTGen {
    private BlockVisitor blockVis = new BlockVisitor();
    private StmtVisitor stmtVis = new StmtVisitor();
    private ExprVisitor exprVis = new ExprVisitor();

    /** Turns a parse tree Prog node into a complete AST.
     * This is called by main()
     */
    public static Stmt gen(ParseTree parseTree) {
        return new ASTGen().blockVis.visit(parseTree);
    }

    class BlockVisitor extends Visitor<Stmt.Block> {
        @Override
        public Stmt.Block visitStart(Grammar.StartContext ctx) {
            return visit(ctx.program());
        }

        @Override
        public Stmt.Block visitProgStmt(Grammar.ProgStmtContext ctx) {
            Stmt first = stmtVis.visit(ctx.statement());
            Stmt.Block rest = visit(ctx.program());
            if (first != null) rest.children().add(0, first);
            return rest;
        }

        @Override
        public Stmt.Block visitEmptyProg(Grammar.EmptyProgContext ctx) {
            return new Stmt.Block(new LinkedList<>());
        }

        @Override
        public Stmt.Block visitBody(Grammar.BodyContext ctx) {
            return visit(ctx.program());
        }
    }

    class StmtVisitor extends Visitor<Stmt> {
        @Override
        public Stmt visitEmitStmt(Grammar.EmitStmtContext ctx) {
            return new Stmt.Print(exprVis.visit(ctx.expr()));
        }

        @Override
        public Stmt visitAssignStmt(Grammar.AssignStmtContext ctx) {
            String varName = ctx.VAR().getText();
            Expr valueExp = exprVis.visit(ctx.expr());
            return new Stmt.Assign(varName, valueExp);
        }

        @Override
        public Stmt visitInputStmt(Grammar.InputStmtContext ctx) {
            String varName = ctx.VAR().getText();
            return new Stmt.Assign(varName, new Expr.Input());
        }

        @Override
        public Stmt visitIfStmt(Grammar.IfStmtContext ctx) {
            Expr cond = exprVis.visit(ctx.expr());
            Stmt.Block body = blockVis.visit(ctx.block());
            return new Stmt.IfElse(cond, body, null);
        }

        @Override
        public Stmt visitIfElseStmt(Grammar.IfElseStmtContext ctx) {
            Expr cond = exprVis.visit(ctx.expr());
            Stmt.Block ifBody = blockVis.visit(ctx.block(0));
            Stmt.Block elseBody = blockVis.visit(ctx.block(1));
            return new Stmt.IfElse(cond, ifBody, elseBody);
        }

        @Override
        public Stmt visitWhileStmt(Grammar.WhileStmtContext ctx) {
            Expr cond = exprVis.visit(ctx.expr());
            Stmt.Block body = blockVis.visit(ctx.block());
            return new Stmt.While(cond, body);
        }
    }

    class ExprVisitor extends Visitor<Expr> {
        @Override
        public Expr visitReverse(Grammar.ReverseContext ctx) {
            return new Expr.Reverse(visit(ctx.expr()));
        }

        @Override
        public Expr visitInvert(Grammar.InvertContext ctx) {
            return new Expr.Not(visit(ctx.expr()));
        }

        @Override
        public Expr visitRevealBool(Grammar.RevealBoolContext ctx) {
            return new Expr.BoolToString(visit(ctx.expr()));
        }

        @Override
        public Expr visitSever(Grammar.SeverContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.Chop(lhs, rhs);
        }

        @Override
        public Expr visitConcat(Grammar.ConcatContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.Concat(lhs, rhs);
        }

        @Override
        public Expr visitLess(Grammar.LessContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.StrLess(lhs, rhs);
        }

        @Override
        public Expr visitOr(Grammar.OrContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.Or(lhs, rhs);
        }

        @Override
        public Expr visitParen(Grammar.ParenContext ctx) {
            return visit(ctx.expr());
        }

        @Override
        public Expr visitStr(Grammar.StrContext ctx) {
            String raw = ctx.STRING().getText();
            StringBuilder contents = new StringBuilder();
            for (int i = 1; i < raw.length() - 1; ++i) {
                char c = raw.charAt(i);
                if (c == '\\') c = raw.charAt(++i);
                contents.append(c);
            }
            return new Expr.Literal(new Value.Str(contents.toString()));
        }

        @Override
        public Expr visitBool(Grammar.BoolContext ctx) {
            boolean val = ctx.BOOL().getText().equals("light");
            return new Expr.Literal(new Value.Bool(val));
        }

        @Override
        public Expr visitVar(Grammar.VarContext ctx) {
            return new Expr.Var(ctx.VAR().getText());
        }
    }
}
