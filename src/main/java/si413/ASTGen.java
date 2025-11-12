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
        return new ASTGen().stlVis.visit(parseTree);
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
        public Stmt.Block visitRegProg(ParseRules.RegProgContext ctx) {
            return visit(ctx.stmtList());
        }

        @Override
        public Stmt.Block visitRegStmtList(ParseRules.RegStmtListContext ctx) {
            List<Stmt> children = new ArrayList<>();
            children.add(stVis.visit(ctx.stmt()));
            children.addAll(visit(ctx.stmtList()).children());
            return new Stmt.Block(children);
        }

        @Override
        public Stmt.Block visitEmptyStmt(ParseRules.EmptyStmtContext ctx) {
            return new Stmt.Block(List.of());
        }
    }

    private class StmtVisitor extends Visitor<Stmt> {
        @Override
        public Stmt visitAssignStr(ParseRules.AssignStrContext ctx) {
            return new Stmt.Assign(ctx.ID().getText(), eVis.visit(ctx.strEx()));
        }

        @Override
        public Stmt visitAssignBool(ParseRules.AssignBoolContext ctx) {
            return new Stmt.Assign(ctx.ID().getText(), eVis.visit(ctx.boolEx()));
        }

        @Override
        public Stmt visitPrintStr(ParseRules.PrintStrContext ctx) {
            return new Stmt.Print(eVis.visit(ctx.strEx()));
        }

        @Override
        public Stmt visitPrintBool(ParseRules.PrintBoolContext ctx) {
            return new Stmt.Print(eVis.visit(ctx.boolEx()));
        }

        @Override
        public Stmt visitIfElse(ParseRules.IfElseContext ctx) {
            return new Stmt.IfElse(
                eVis.visit(ctx.boolEx()),
                stlVis.visit(ctx.stmtList(0)),
                stlVis.visit(ctx.stmtList(1)));
        }

        @Override
        public Stmt visitWhileLoop(ParseRules.WhileLoopContext ctx) {
            return new Stmt.While(
                eVis.visit(ctx.boolEx()),
                stlVis.visit(ctx.stmtList()));
        }

    }

    private class ExprVisitor extends Visitor<Expr> {
        @Override
        public Expr visitStrIdentity(ParseRules.StrIdentityContext ctx) {
            return visit(ctx.strEx());
        }

        @Override
        public Expr visitInput(ParseRules.InputContext ctx) {
            return new Expr.Input();
        }

        @Override
        public Expr visitReverse(ParseRules.ReverseContext ctx) {
            return new Expr.Reverse(visit(ctx.strEx()));
        }

        @Override
        public Expr visitConcat(ParseRules.ConcatContext ctx) {
            return new Expr.Concat(
                visit(ctx.strEx(0)),
                visit(ctx.strEx(1)));
        }

        @Override
        public Expr visitStrLit(ParseRules.StrLitContext ctx) {
            StringBuilder sb = new StringBuilder();
            String raw = ctx.STR().getText();
            for (int i = 1; i < raw.length()-1; ++i) {
                sb.append(raw.charAt(i));
            }
            return new Expr.StringLit(sb.toString());
        }

        @Override
        public Expr visitStrVar(ParseRules.StrVarContext ctx) {
            return new Expr.Var(ctx.ID().getText());
        }

        @Override
        public Expr visitAnd(ParseRules.AndContext ctx) {
            return new Expr.And(
                visit(ctx.boolEx(0)),
                visit(ctx.boolEx(1)));
        }

        @Override
        public Expr visitOr(ParseRules.OrContext ctx) {
            return new Expr.Or(
                visit(ctx.boolEx(0)),
                visit(ctx.boolEx(1)));
        }

        @Override
        public Expr visitNot(ParseRules.NotContext ctx) {
            return new Expr.Not(
                visit(ctx.boolEx()));
        }

        @Override
        public Expr visitContains(ParseRules.ContainsContext ctx) {
            return new Expr.Contains(
                visit(ctx.strEx(0)),
                visit(ctx.strEx(1)));
        }

        @Override
        public Expr visitLessThan(ParseRules.LessThanContext ctx) {
            return new Expr.StrLess(
                visit(ctx.strEx(0)),
                visit(ctx.strEx(1)));
        }

        @Override
        public Expr visitBoolVar(ParseRules.BoolVarContext ctx) {
            return new Expr.Var(ctx.ID().getText());
        }

        @Override
        public Expr visitBoolLit(ParseRules.BoolLitContext ctx) {
            return new Expr.BoolLit(ctx.BOOL().getText().equals("T"));
        }

        @Override
        public Expr visitBoolIdentity(ParseRules.BoolIdentityContext ctx) {
            return visit(ctx.boolEx());
        }
    }

    private StmtListVisitor stlVis = new StmtListVisitor();
    private StmtVisitor stVis = new StmtVisitor();
    private ExprVisitor eVis = new ExprVisitor();
}
