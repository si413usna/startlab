package si413;

import java.util.List;
import java.util.ArrayList;
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
        public Stmt.Block visitProgramRoot(Grammar.ProgramRootContext ctx) {
            return visit(ctx.stmt_list());
        }

        @Override
        public Stmt.Block visitStmtListRec(Grammar.StmtListRecContext ctx) {
            Stmt first = stmtVis.visit(ctx.stmt());
            Stmt.Block rest = visit(ctx.stmt_list());
            if (first != null) rest.children().add(0, first);
            return rest;
        }

        @Override
        public Stmt.Block visitStmtListEmpty(Grammar.StmtListEmptyContext ctx) {
            return new Stmt.Block(new java.util.LinkedList<>());
        }
    }

    class StmtVisitor extends Visitor<Stmt> {
        @Override
        public Stmt visitBloomStmt(Grammar.BloomStmtContext ctx) {
            return new Stmt.Print(exprVis.visit(ctx.expr()));
        }

        @Override
        public Stmt visitSeedStmt(Grammar.SeedStmtContext ctx) {
            String varName = ctx.VAR().getText();
            Expr valueExp = exprVis.visit(ctx.expr());
            return new Stmt.Assign(varName, valueExp);
        }

        @Override
        public Stmt visitSparkStmt(Grammar.SparkStmtContext ctx) {
            Expr cond = exprVis.visit(ctx.expr());
            Stmt.Block body = blockVis.visit(ctx.stmt_list());
            return new Stmt.IfElse(cond, body, null);
        }

        @Override
        public Stmt visitSparkEmberStmt(Grammar.SparkEmberStmtContext ctx) {
            Expr cond = exprVis.visit(ctx.expr());
            Stmt.Block ifBody = blockVis.visit(ctx.stmt_list(0));
            Stmt.Block elseBody = blockVis.visit(ctx.stmt_list(1));
            return new Stmt.IfElse(cond, ifBody, elseBody);
        }

        @Override
        public Stmt visitKindleStmt(Grammar.KindleStmtContext ctx) {
            Expr cond = exprVis.visit(ctx.expr());
            Stmt.Block body = blockVis.visit(ctx.stmt_list());
            return new Stmt.While(cond, body);
        }

        @Override
        public Stmt visitExprStmt(Grammar.ExprStmtContext ctx) {
            return new Stmt.ExprStmt(exprVis.visit(ctx.expr()));
        }

        @Override
        public Stmt visitEmptyStmt(Grammar.EmptyStmtContext ctx) {
            return null;
        }
    }

    class ExprVisitor extends Visitor<Expr> {
        @Override
        public Expr visitParenExpr(Grammar.ParenExprContext ctx) {
            return visit(ctx.expr());
        }

        @Override
        public Expr visitRevrExpr(Grammar.RevrExprContext ctx) {
            return new Expr.Reverse(visit(ctx.expr()));
        }

        @Override
        public Expr visitNotExpr(Grammar.NotExprContext ctx) {
            return new Expr.Not(visit(ctx.expr()));
        }

        @Override
        public Expr visitB2SExpr(Grammar.B2SExprContext ctx) {
            return new Expr.BoolToString(visit(ctx.expr()));
        }

        @Override
        public Expr visitChopExpr(Grammar.ChopExprContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.Chop(lhs, rhs);
        }

        @Override
        public Expr visitPlusExpr(Grammar.PlusExprContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.Concat(lhs, rhs);
        }

        @Override
        public Expr visitLessThanExpr(Grammar.LessThanExprContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.StrLess(lhs, rhs);
        }

        @Override
        public Expr visitOrExpr(Grammar.OrExprContext ctx) {
            Expr lhs = visit(ctx.expr(0));
            Expr rhs = visit(ctx.expr(1));
            return new Expr.Or(lhs, rhs);
        }

        @Override
        public Expr visitHarvestExpr(Grammar.HarvestExprContext ctx) {
            return new Expr.Input();
        }

        @Override
        public Expr visitStringLitExpr(Grammar.StringLitExprContext ctx) {
            String raw = ctx.STRINGLIT().getText();
            StringBuilder contents = new StringBuilder();
            for (int i = 1; i < raw.length() - 1; ++i) {
                char c = raw.charAt(i);
                if (c == '~') c = raw.charAt(++i);
                contents.append(c);
            }
            return new Expr.Literal(new Value.Str(contents.toString()));
        }

        @Override
        public Expr visitLightExpr(Grammar.LightExprContext ctx) {
            return new Expr.Literal(new Value.Bool(true));
        }

        @Override
        public Expr visitDarkExpr(Grammar.DarkExprContext ctx) {
            return new Expr.Literal(new Value.Bool(false));
        }

        @Override
        public Expr visitVarExpr(Grammar.VarExprContext ctx) {
            return new Expr.Var(ctx.VAR().getText());
        }
    }
}
