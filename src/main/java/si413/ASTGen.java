package si413;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;

/** This class is used to create the AST from a parse tree.
 * The static method ASTGen.gen(parseTree) is the specific function
 * to perform that conversion.
 */
public class ASTGen {
    /** Turns a parse tree Prog node into a complete AST.
     * This is the main external interface for the ASTGen class.
     */
    public static Stmt.Block gen(ParseRules.ProgContext parseTree) {
        //return new ASTGen().stlVis.visit(parseTree);
        return null;
    }

    /** Use this as the subclass for the visitor classes.
     * It warns you if one of the visit methods is missing at parse-time.
     */
    private static class Visitor<T> extends ParseRulesBaseVisitor<T> {
        @Override
        public T visitChildren(org.antlr.v4.runtime.tree.RuleNode node) {
            return Errors.error(String.format(
                "class %s has no visit method for %s",
                getClass().getSimpleName(),
                node.getClass().getSimpleName()));
        }
    }

    private class StmtListVisitor extends Visitor<Stmt.Block> {
        @Override
        public Stmt.Block visitRegularProg(ParseRules.RegularProgContext ctx) {
            List<Stmt> children = new ArrayList<>();
            children.add(stVis.visit(ctx.stat()));
            children.addAll(visit(ctx.prog()).children());
            return new Stmt.Block(children);
        }

        @Override
        public Stmt.Block visitEmptyProg(ParseRules.EmptyProgContext ctx) {
            return new Stmt.Block(List.of());
        }

        @Override
        public Stmt.Block visitLRBracket(ParseRules.LRBracketContext ctx) {
            return visit(ctx.inner());
        }

        @Override
        public Stmt.Block visitInnerInner(ParseRules.InnerInnerContext ctx) {
            List<Stmt> children = new ArrayList<>();
            children.add(stVis.visit(ctx.stat()));
            children.addAll(visit(ctx.inner()).children());
            return new Stmt.Block(children);
        }

        @Override
        public Stmt.Block visitEmptyInner(ParseRules.EmptyInnerContext ctx) {
            return new Stmt.Block(List.of());
        }
    }

    private class StmtVisitor extends Visitor<Stmt> {
        @Override
        public Stmt visitPrintStat(ParseRules.PrintStatContext ctx) {
            return new Stmt.Print(eVis.visit(ctx.expr()));
        }

        @Override
        public Stmt visitIDStat(ParseRules.IDStatContext ctx) {
            if (!ctx.ID(0).getText().equals(ctx.ID(1).getText())) {
                return Errors.error(String.format("ID assignment mismatch: '%s' and '%s'",
                            ctx.ID(0).getText(), ctx.ID(1).getText()));
            }
            else return new Stmt.Assign(ctx.ID(0).getText(), eVis.visit(ctx.expr()));
        }

        @Override
        public Stmt visitIFStat(ParseRules.IFStatContext ctx) {
            return new Stmt.IfElse(
                eVis.visit(ctx.expr()),
                stlVis.visit(ctx.bracket()),
                new Stmt.Block(List.of()));
        }

        @Override
        public Stmt visitWHILEStat(ParseRules.WHILEStatContext ctx) {
            return new Stmt.While(
                eVis.visit(ctx.expr()),
                stlVis.visit(ctx.bracket()));
        }
    }

    private class ExprVisitor extends Visitor<Expr> {
        @Override
        public Expr visitLitExpr(ParseRules.LitExprContext ctx) {
            StringBuilder sb = new StringBuilder();
            String raw = ctx.LIT().getText();
            for (int i = 1; i < raw.length()-1; ++i) {
                sb.append(raw.charAt(i));
            }
            return new Expr.StringLit(sb.toString());
        }

        @Override
        public Expr visitInputExpr(ParseRules.InputExprContext ctx) {
            return new Expr.Input();
        }

        @Override
        public Expr visitRevExpr(ParseRules.RevExprContext ctx) {
            return new Expr.Reverse(visit(ctx.expr()));
        }

        @Override
        public Expr visitIDExpr(ParseRules.IDExprContext ctx) {
            return new Expr.Var(ctx.ID().getText());
        }

        @Override
        public Expr visitBoolLitExpr(ParseRules.BoolLitExprContext ctx) {
            return new Expr.BoolLit(ctx.BOOL().getText().equals("Cooked"));
        }

        @Override
        public Expr visitConcatExpr(ParseRules.ConcatExprContext ctx) {
            return new Expr.Concat(
                visit(ctx.expr(0)),
                visit(ctx.expr(1)));
        }

        @Override
        public Expr visitParenExpr(ParseRules.ParenExprContext ctx) {
            return visit(ctx.expr());
        }

        @Override
        public Expr visitNotExpr(ParseRules.NotExprContext ctx) {
            return new Expr.Not(visit(ctx.expr()));
        }

        @Override
        public Expr visitOpExpr(ParseRules.OpExprContext ctx) {
            Expr left = visit(ctx.expr(0));
            Expr right = visit(ctx.expr(1));
            String op = ctx.OP().getText();
            if (op.equals("<")) {
                return new Expr.StrLess(left, right);
            }
            else if (op.equals(">")) {
                return new Expr.StrLess(right, left);
            }
            else if (op.equals("?")) {
                return new Expr.Contains(left, right);
            }
            else if (op.equals("&")) {
                return new Expr.And(left, right);
            }
            else if (op.equals("|")) {
                return new Expr.Or(left, right);
            }
            else throw new AssertionError("illegal op; should be unreachable");
        }
    }

    private StmtListVisitor stlVis = new StmtListVisitor();
    private StmtVisitor stVis = new StmtVisitor();
    private ExprVisitor eVis = new ExprVisitor();
}
