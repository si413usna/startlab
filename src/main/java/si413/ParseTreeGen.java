package si413;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.Method;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/** Parse tree generator.
 * The tokens and grammar come from src/main/resource/si413/tokenSpec.txt
 * and src/main/antlr4/si413/ParseRules.g4 respectively.
 */
public class ParseTreeGen {
    private Vocabulary vocab;
    private Tokenizer tokenizer;

    public ParseTreeGen() throws IOException {
        vocab = ParseRules.VOCABULARY;
        this.tokenizer = new Tokenizer(
            getClass().getResourceAsStream("tokenSpec.txt"),
            vocab
        );
    }

    public TokenStream getStream(Path sourceFile) throws IOException {
        return tokenizer.streamFrom(sourceFile);
    }

    public void showTokens(TokenStream source) throws IOException {
        source.seek(0);
        int mark = source.mark();
        System.out.println("================================");
        System.out.println("         TOKEN STREAM");
        System.out.println("================================");
        System.out.println("line\tcolumn\ttype\tspelling");
        System.out.println("------- ------- ------- -------");
        while (true) {
            Token tok = source.LT(1);
            System.out.format("%d\t%d\t%s\t%s\n",
                tok.getLine(),
                tok.getCharPositionInLine(),
                vocab.getSymbolicName(tok.getType()),
                tok.getText()
            );
            if (tok.getType() == -1) break;
            source.consume();
        }
        source.seek(0);
        source.release(mark);
    }

    private void printNode(ParseTree curNode, String prefix) {
        if (curNode instanceof TerminalNode tnode) {
            Token tok = tnode.getSymbol();
            System.out.format("%s '%s'\n",
                vocab.getSymbolicName(tok.getType()),
                tok.getText());
        }
        else if (curNode instanceof ParserRuleContext inode) {
            String nonterminal = ParseRules.ruleNames[inode.getRuleIndex()];
            String context = inode.getClass().getSimpleName();
            System.out.format("%s %s\n", nonterminal, context);
        }
        else { assert false; }
        int nchildren = curNode.getChildCount();
        for (int i = 0; i < nchildren; ++i) {
            boolean last = (i == nchildren - 1);
            System.out.print(prefix + (last ? "└── " : "├── "));
            printNode(curNode.getChild(i), prefix + (last ? "    " : "│   "));
        }
    }

    public void showParseTree(TokenStream source) throws IOException {
        source.seek(0);
        int mark = source.mark();
        System.out.println("================================");
        System.out.println("         PARSE TREE");
        System.out.println("================================");
        ParseRules parser = new ParseRules(source);
        Errors.register(parser);
        String startRuleName = parser.getRuleNames()[0];
        ParseTree root = null;
        try {
            Method startRuleMethod = parser.getClass().getMethod(startRuleName);
            root = (ParseTree)startRuleMethod.invoke(parser);
        }
        catch (Exception e) { throw new RuntimeException(e); }
        printNode(root, "");
        source.seek(0);
        source.release(mark);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            Errors.error("need 1 command-line arg: input source file");
        }
        Path sourceFile = Path.of(args[0]);
        ParseTreeGen ptgen = new ParseTreeGen();
        TokenStream toks = ptgen.getStream(sourceFile);
        ptgen.showTokens(toks);
        ptgen.showParseTree(toks);
        System.out.println("================================");
    }
}
