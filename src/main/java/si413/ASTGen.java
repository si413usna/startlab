package si413;

import java.util.List;
import java.util.ArrayList;

/** This class is used to create the AST from a parse tree.
 * The static method ASTGen.gen(parseTree) is the specific function
 * to perform that conversion.
 */
public class ASTGen {
    /** Turns a parse tree Prog node into a complete AST.
     * This is the main external interface for the ASTGen class.
     */
    public static Stmt.Block gen(ParseRules.ProgContext ptreeRoot) {
        return new ASTGen().progVis.visit(ptreeRoot);
    }


    private class ProgVisitor extends Visitor<Stmt.Block> {
        @Override
        public Stmt.Block visitRegularProg(ParseRules.RegularProgContext ctx) {
            // recursively call visit to get the first statement and block for the rest
            Stmt first = stmtVis.visit(ctx.stmt());
            Stmt.Block rest = visit(ctx.prog());
            // combine those into a single block AST node
            List<Stmt> children = new ArrayList<>();
            children.add(first);
            children.addAll(rest.children());
            return new Stmt.Block(children);
        }

        @Override
        public Stmt.Block visitEmptyProg(ParseRules.EmptyProgContext ctx) {
            return new Stmt.Block(List.of());
        }
    }


    private class StmtVisitor extends Visitor<Stmt> {
        @Override
        public Stmt visitStringPrint(ParseRules.StringPrintContext ctx) {
            Expr<String> child = strExprVis.visit(ctx.strExpr());
            return new Stmt.PrintString(child);
        }

        @Override
        public Stmt visitBoolPrint(ParseRules.BoolPrintContext ctx) {
            Expr<Boolean> child = boolExprVis.visit(ctx.boolExpr());
            return new Stmt.PrintBool(child);
        }
    }


    private class StringExprVisitor extends Visitor<Expr<String>> {
        @Override
        public Expr<String> visitStrLit(ParseRules.StrLitContext ctx) {
            // extract the actual string literal without escapes
            StringBuilder sb = new StringBuilder();
            String raw = ctx.STRLIT().getText();
            for (int i = 1; i < raw.length()-1; ++i) {
                if (raw.charAt(i) == '\\') ++i;
                sb.append(raw.charAt(i));
            }
            return new Expr.StringLit(sb.toString());
        }

        @Override
        public Expr<String> visitConcat(ParseRules.ConcatContext ctx) {
            Expr<String> lhs = visit(ctx.strExpr(0));
            Expr<String> rhs = visit(ctx.strExpr(1));
            return new Expr.Concat(lhs, rhs);
        }
    }


    private class BoolExprVisitor extends Visitor<Expr<Boolean>> {
        @Override
        public Expr<Boolean> visitBoolLit(ParseRules.BoolLitContext ctx) {
            return new Expr.BoolLit(ctx.BOOLLIT().getText().equals("True"));
        }

        @Override
        public Expr<Boolean> visitBoolNot(ParseRules.BoolNotContext ctx) {
            Expr<Boolean> child = visit(ctx.boolExpr());
            return new Expr.Not(child);
        }
    }


    private ProgVisitor progVis = new ProgVisitor();
    private StmtVisitor stmtVis = new StmtVisitor();
    private StringExprVisitor strExprVis = new StringExprVisitor();
    private BoolExprVisitor boolExprVis = new BoolExprVisitor();


    /** Use this as the subclass for the visitor classes.
     * It overrides the default method to alert you if one of the
     * visit methods is missing.
     */
    private static class Visitor<T> extends ParseRulesBaseVisitor<T> {
        // This overrides the default behavior to alert if a visit method is missing.
        @Override
        public T visitChildren(org.antlr.v4.runtime.tree.RuleNode node) {
            return Errors.error(String.format(
                "class %s has no visit method for %s",
                getClass().getSimpleName(),
                node.getClass().getSimpleName()));
        }
    }
}
