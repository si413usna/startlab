package si413;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.lang.reflect.Method;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    /** Calls the Tokenizer to extract tokens from the source text file. */
    public static TokenStream tokenize(Path sourceFile) throws IOException {
        return new Tokenizer(
            Main.class.getResourceAsStream("tokenSpec.txt"),
            Grammar.VOCABULARY
        ).streamFrom(sourceFile);
    }

    /** Calls the ANTLR-generated parser to form the tokens into a parse tree. */
    public static ParseTree parse(TokenStream tokens) {
        Grammar parser = new Grammar(tokens);
        Errors.register(parser);
        try {
            return (ParseTree) Grammar.class.getMethod(Grammar.ruleNames[0]).invoke(parser);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("problem invoking start rule method from parser: " + e);
        }
    }

    private static void usage() {
        System.err.format(
            """
            usage: java -jar calc [option] sourceFile [destFile]
            where option is one of
                -t: show token stream
                -p: show parse tree
                -a: show AST
                -i: (default with one arg) run the interpreter
                -c: (default with two args) run the compiler
            """);
        System.exit(2);
    }

    private enum Command {
        TOKENS, PTREE, AST, INTERP, COMPILE
    }

    public static void main(String[] args) throws IOException {
        int nargs = args.length;
        Command command = nargs == 1 ? Command.INTERP : Command.COMPILE;
        String ifname = null;
        String ofname = null;

        int argind = 0;
        if (nargs >= 1 && args[0].startsWith("-")) {
            switch (args[0].charAt(1)) {
                case 't':
                    command = Command.TOKENS;
                    nargs = 1;
                    break;
                case 'p':
                    command = Command.PTREE;
                    nargs = 1;
                    break;
                case 'a':
                    command = Command.AST;
                    nargs = 1;
                    break;
                case 'i':
                    command = Command.INTERP;
                    nargs = 1;
                    break;
                case 'c':
                    command = Command.COMPILE;
                    nargs = 2;
                    break;
                default:
                    usage();
            }
            ++argind;
        }

        if (args.length != argind + nargs) usage();

        if (argind < args.length) ifname = args[argind++];
        if (argind < args.length) ofname = args[argind++];

        Tokenizer tokenizer = new Tokenizer(
            Main.class.getResourceAsStream("tokenSpec.txt"),
            Grammar.VOCABULARY);

        TokenStream tokens = null;
        try { tokens = tokenize(Path.of(ifname)); }
        catch (NoSuchFileException e) {
            System.err.format("File not found: %s\n", ifname);
            System.exit(2);
        }

        if (command == Command.TOKENS) {
            new SyntaxViz(tokenizer).showTokens(tokens);
            System.exit(0);
        }

        ParseTree ptree = parse(tokens);

        if (command == Command.PTREE) {
            new SyntaxViz(tokenizer).showParseTree(ptree);
            System.exit(0);
        }

        Stmt astRoot = ASTGen.gen(ptree);

        if (command == Command.AST) {
            SyntaxViz.showAST(astRoot);
            System.exit(0);
        }

        if (command == Command.INTERP) {
            astRoot.exec(RunState.create());
        }

        if (command == Command.COMPILE) {
            throw new UnsupportedOperationException("no compiler yet!");
        }

        System.exit(0);
    }
}
