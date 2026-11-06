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
        public Stmt.Block visitBegin(Grammar.BeginContext ctx) {
            return visit(ctx.program());
        }

        @Override
        public Stmt.Block visitPrgm(Grammar.PrgmContext ctx) {
            Stmt first = stmtVis.visit(ctx.stmt());
            Stmt.Block rest = visit(ctx.program());
            if (first != null) rest.children().add(0, first);
            return rest;
        }

        @Override
        public Stmt.Block visitEmptyprgm(Grammar.EmptyprgmContext ctx) {
            return new Stmt.Block(new LinkedList<>());
        }
    }

    class StmtVisitor extends Visitor<Stmt> {
        @Override
        public Stmt visitPrintstatement(Grammar.PrintstatementContext ctx) {
            return new Stmt.Print(exprVis.visit(ctx.expression()));
        }

        @Override
        public Stmt visitVarassign(Grammar.VarassignContext ctx) {
            String varName = ctx.ID().getText();
            Expr valueExp = exprVis.visit(ctx.expression());
            return new Stmt.Assign(varName, valueExp);
        }

        @Override
        public Stmt visitParenexpression(Grammar.ParenexpressionContext ctx) {
            return new Stmt.ExprStmt(exprVis.visit(ctx.expression()));
        }

        @Override
        public Stmt visitParenifstatement(Grammar.ParenifstatementContext ctx) {
            return visit(ctx.ifStatement());
        }

        @Override
        public Stmt visitIfbody(Grammar.IfbodyContext ctx) {
            Expr cond = exprVis.visit(ctx.expression());
            Stmt.Block ifBody = blockVis.visit(ctx.program(0));
            Stmt.Block elseBody = blockVis.visit(ctx.program(1));
            return new Stmt.IfElse(cond, ifBody, elseBody);
        }

        @Override
        public Stmt visitWhileloop(Grammar.WhileloopContext ctx) {
            return visit(ctx.whileStatement());
        }

        @Override
        public Stmt visitWhilebody(Grammar.WhilebodyContext ctx) {
            Expr cond = exprVis.visit(ctx.expression());
            Stmt.Block body = blockVis.visit(ctx.program());
            return new Stmt.While(cond, body);
        }
    }

    class ExprVisitor extends Visitor<Expr> {
        @Override
        public Expr visitNotexpr(Grammar.NotexprContext ctx) {
            return visit(ctx.notExpression());
        }

        @Override
        public Expr visitThingexpr(Grammar.ThingexprContext ctx) {
            return visit(ctx.thingExpression());
        }

        @Override
        public Expr visitOrexpression(Grammar.OrexpressionContext ctx) {
            Expr lhs = visit(ctx.thingExpression());
            Expr rhs = visit(ctx.notExpression());
            return new Expr.Or(lhs, rhs);
        }

        @Override
        public Expr visitNotthingexpr(Grammar.NotthingexprContext ctx) {
            return new Expr.Not(visit(ctx.thingExpression()));
        }

        @Override
        public Expr visitThingexprvalue(Grammar.ThingexprvalueContext ctx) {
            return visit(ctx.thing());
        }

        @Override
        public Expr visitBoolliteral(Grammar.BoolliteralContext ctx) {
            boolean val = ctx.BOOLLIT().getText().equals("TRUE");
            return new Expr.Literal(new Value.Bool(val));
        }

        @Override
        public Expr visitStrliteral(Grammar.StrliteralContext ctx) {
            String raw = ctx.STRLIT().getText();
            StringBuilder contents = new StringBuilder();
            for (int i = 1; i < raw.length() - 1; ++i) {
                char c = raw.charAt(i);
                if (c == '$') c = raw.charAt(++i);
                contents.append(c);
            }
            return new Expr.Literal(new Value.Str(contents.toString()));
        }

        @Override
        public Expr visitIdentifier(Grammar.IdentifierContext ctx) {
            return new Expr.Var(ctx.ID().getText());
        }

        @Override
        public Expr visitCmd(Grammar.CmdContext ctx) {
            return visit(ctx.command());
        }

        @Override
        public Expr visitParenthing(Grammar.ParenthingContext ctx) {
            return visit(ctx.expression());
        }

        @Override
        public Expr visitInput(Grammar.InputContext ctx) {
            return new Expr.Input();
        }

        @Override
        public Expr visitReverse(Grammar.ReverseContext ctx) {
            return new Expr.Reverse(visit(ctx.expression()));
        }

        @Override
        public Expr visitConcatenation(Grammar.ConcatenationContext ctx) {
            Expr lhs = visit(ctx.expression(0));
            Expr rhs = visit(ctx.expression(1));
            return new Expr.Concat(lhs, rhs);
        }

        @Override
        public Expr visitStrcomp(Grammar.StrcompContext ctx) {
            Expr lhs = visit(ctx.expression(0));
            Expr rhs = visit(ctx.expression(1));
            return new Expr.StrLess(lhs, rhs);
        }

        @Override
        public Expr visitBoolconversion(Grammar.BoolconversionContext ctx) {
            return new Expr.BoolToString(visit(ctx.expression()));
        }

        @Override
        public Expr visitChop(Grammar.ChopContext ctx) {
            Expr lhs = visit(ctx.expression(0));
            Expr rhs = visit(ctx.expression(1));
            return new Expr.Chop(lhs, rhs);
        }
    }
}
