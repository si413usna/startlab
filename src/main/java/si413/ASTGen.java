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
        return new ASTGen().progVis.visit(parseTree);
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

    private class ProgVisitor extends Visitor<Stmt.Block> {
        @Override
        public Stmt.Block visitProgram(ParseRules.ProgramContext ctx) {
            return new Stmt.Block(stlVis.visit(ctx.stmtList()));
        }
    }

    private class StmtListVisitor extends Visitor<List<Stmt>> {
        @Override
        public List<Stmt> visitMultiStmts(ParseRules.MultiStmtsContext ctx) {
            List<Stmt> res = new ArrayList<>();
            res.add(stVis.visit(ctx.stmt()));
            res.addAll(visit(ctx.stmtList()));
            return res;
        }

        @Override
        public List<Stmt> visitNoStmts(ParseRules.NoStmtsContext ctx) {
            return List.of();
        }
    }

    private class StmtVisitor extends Visitor<Stmt> {
        @Override
        public Stmt visitDisplay(ParseRules.DisplayContext ctx) {
            return new Stmt.Print(exprVis.visit(ctx.expr()));
        }

        @Override
        public Stmt visitStore(ParseRules.StoreContext ctx) {
            return new Stmt.Assign(
                ctx.VARNAME().getText(),
                exprVis.visit(ctx.expr()));
        }

        @Override
        public Stmt visitIfOnly(ParseRules.IfOnlyContext ctx) {
            return new Stmt.IfElse(
                exprVis.visit(ctx.expr()),
                new Stmt.Block(stlVis.visit(ctx.stmtList())),
                new Stmt.Block(List.of()));
        }

        @Override
        public Stmt visitIfElse(ParseRules.IfElseContext ctx) {
            return new Stmt.IfElse(
                exprVis.visit(ctx.expr()),
                new Stmt.Block(stlVis.visit(ctx.stmtList(0))),
                new Stmt.Block(stlVis.visit(ctx.stmtList(1))));
        }

        @Override
        public Stmt visitWhile(ParseRules.WhileContext ctx) {
            return new Stmt.While(
                exprVis.visit(ctx.expr()),
                new Stmt.Block(stlVis.visit(ctx.stmtList())));
        }

        @Override
        public Stmt visitExprStmt(ParseRules.ExprStmtContext ctx) {
            return new Stmt.ExprStmt(exprVis.visit(ctx.expr()));
        }
    }

    private class ParamListVisitor extends Visitor<List<String>> {
        @Override
        public List<String> visitManyParams(ParseRules.ManyParamsContext ctx) {
            List<String> res = new ArrayList<>();
            res.add(ctx.VARNAME().getText());
            res.addAll(visit(ctx.paramList()));
            return res;
        }

        @Override
        public List<String> visitOneParam(ParseRules.OneParamContext ctx) {
            return List.of(ctx.VARNAME().getText());
        }

        @Override
        public List<String> visitNoParams(ParseRules.NoParamsContext ctx) {
            return List.of();
        }
    }

    private class ExprVisitor extends Visitor<Expr> {
        @Override
        public Expr visitParenExpr(ParseRules.ParenExprContext ctx) {
            return visit(ctx.expr());
        }

        @Override
        public Expr visitAnd(ParseRules.AndContext ctx) {
            return new Expr.And(
                visit(ctx.expr(0)),
                visit(ctx.expr(1)));
        }

        @Override
        public Expr visitOr(ParseRules.OrContext ctx) {
            return new Expr.Or(
                visit(ctx.expr(0)),
                visit(ctx.expr(1)));
        }

        @Override
        public Expr visitLess(ParseRules.LessContext ctx) {
            return new Expr.StrLess(
                visit(ctx.expr(0)),
                visit(ctx.expr(1)));
        }

        @Override
        public Expr visitGreater(ParseRules.GreaterContext ctx) {
            return new Expr.StrLess(
                visit(ctx.expr(1)),
                visit(ctx.expr(0)));
        }

        @Override
        public Expr visitContains(ParseRules.ContainsContext ctx) {
            return new Expr.Contains(
                visit(ctx.expr(0)),
                visit(ctx.expr(1)));
        }

        @Override
        public Expr visitConcat(ParseRules.ConcatContext ctx) {
            return new Expr.Concat(
                visit(ctx.expr(0)),
                visit(ctx.expr(1)));
        }

        @Override
        public Expr visitReverse(ParseRules.ReverseContext ctx) {
            return new Expr.Reverse(visit(ctx.expr()));
        }

        @Override
        public Expr visitNot(ParseRules.NotContext ctx) {
            return new Expr.Not(visit(ctx.expr()));
        }

        @Override
        public Expr visitVar(ParseRules.VarContext ctx) {
            return new Expr.Var(ctx.VARNAME().getText());
        }

        @Override
        public Expr visitStrLit(ParseRules.StrLitContext ctx) {
            StringBuilder sb = new StringBuilder();
            String raw = ctx.STRLIT().getText();
            for (int i = 2; i < raw.length()-1; ++i) {
                sb.append(raw.charAt(i));
            }
            return new Expr.StringLit(sb.toString());
        }

        @Override
        public Expr visitBoolLit(ParseRules.BoolLitContext ctx) {
            return new Expr.BoolLit(ctx.BOOLLIT().getText().equals("B[FR]"));
        }

        @Override
        public Expr visitAskString(ParseRules.AskStringContext ctx) {
            return new Expr.Input();
        }

        @Override
        public Expr visitAskBool(ParseRules.AskBoolContext ctx) {
            return new Expr.BInput("FR");
        }
    }

    private class ExprListVisitor extends Visitor<List<Expr>> {
        @Override
        public List<Expr> visitManyArgs(ParseRules.ManyArgsContext ctx) {
            List<Expr> res = new ArrayList<>();
            res.add(exprVis.visit(ctx.expr()));
            res.addAll(visit(ctx.argList()));
            return res;
        }

        @Override
        public List<Expr> visitOneArg(ParseRules.OneArgContext ctx) {
            return List.of(exprVis.visit(ctx.expr()));
        }

        @Override
        public List<Expr> visitNoArgs(ParseRules.NoArgsContext ctx) {
            return List.of();
        }
    }

    private ProgVisitor progVis = new ProgVisitor();
    private StmtListVisitor stlVis = new StmtListVisitor();
    private StmtVisitor stVis = new StmtVisitor();
    private ExprVisitor exprVis = new ExprVisitor();
    private ParamListVisitor plVis = new ParamListVisitor();
    private ExprListVisitor elVis = new ExprListVisitor();
}
