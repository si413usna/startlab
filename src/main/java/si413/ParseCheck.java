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

/** Parse tree checker.  */
public class ParseCheck {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            Errors.error("need 1 command-line arg: input source file");
        }

        Path sourceFile = Path.of(args[0]);

        TokenStream toks = new Tokenizer(
            ParseCheck.class.getResourceAsStream("tokenSpec.txt"),
            ParseRules.VOCABULARY
        ).streamFrom(sourceFile);

        int mark = toks.mark();
        while (true) {
            Token tok = toks.LT(1);
            if (tok.getType() == -1) break;
            toks.consume();
        }
        toks.seek(0);
        toks.release(mark);
        System.out.println("tokenizing OK");

        ParseRules parser = new ParseRules(toks);
        Errors.register(parser);
        String startRuleName = parser.getRuleNames()[0];
        ParseTree root = null;
        try {
            Method startRuleMethod = parser.getClass().getMethod(startRuleName);
            root = (ParseTree)startRuleMethod.invoke(parser);
        }
        catch (Exception e) { throw new RuntimeException(e); }
        System.out.println("parsing OK");
    }
}
