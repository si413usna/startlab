package si413;

/** Use this as the base class any visitor classes.
 * It warns you if one of the visit methods is missing at parse-time.
 * Note, if you want to see all the methods in the corresponding interface,
 * check out target/generated-sources/antlr4/si413/GrammarVisitor.java
 */
class Visitor<T> extends GrammarBaseVisitor<T> {
    @Override
    public T visitChildren(org.antlr.v4.runtime.tree.RuleNode node) {
        return Errors.error(String.format(
            "class %s has no visit method for %s",
            getClass().getSimpleName(),
            node.getClass().getSimpleName()));
    }
}
