package si413;

import java.nio.file.Path;
import java.io.PrintWriter;
import java.io.IOException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Compiler {
    private class StmtVisitor extends ParseRulesBaseVisitor<Void> {
        // TODO your visit methods for statements here!
    }

    private class ExprVisitor extends ParseRulesBaseVisitor<String> {
        // TODO your visit methods for expressions here!
        // (feel free to change the return type from String to something else if you want)
    }

    // TODO probably need a few more fields here
    private StmtVisitor svisitor = new StmtVisitor();
    private ExprVisitor evisitor = new ExprVisitor();
    private PrintWriter dest;

    public Compiler(Path destFile) throws IOException {
        dest = new PrintWriter(destFile.toFile());
    }

    public void compile(ParseTree ptree) throws IOException {
        // TODO copy preamble (helper function definitions) to dest

        dest.println("define i32 @main() {");

        // this calls all of your visit methods to walk the parse tree
        // note that the code emitted goes inside main()
        svisitor.visit(ptree);

        dest.println("  ret i32 0");
        dest.println("}");

        // TODO you probably want to put the string literal definitions
        // down here. They can't be directly emitted from the visit methods
        // because they have to be outside of main().

        dest.close();
    }

    public static TokenStream getTokens(Path sourceFile) throws IOException {
        return new Tokenizer(
            Compiler.class.getResourceAsStream("tokenSpec.txt"),
            ParseRules.VOCABULARY
        ).streamFrom(sourceFile);
    }

    public static ParseTree parse(TokenStream tokens) throws IOException {
        ParseRules parser = new ParseRules(tokens);
        Errors.register(parser);
        return parser.prog();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            Errors.error("need 2 command-line args: source_file dest_file");
        }
        Path sourceFile = Path.of(args[0]);
        Path destFile = Path.of(args[1]);

        TokenStream tokens = getTokens(sourceFile);
        ParseTree ptree = parse(tokens);
        new Compiler(destFile).compile(ptree);
    }
}
