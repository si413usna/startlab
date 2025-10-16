package si413;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.nio.file.Path;
import java.io.IOException;
import org.antlr.v4.runtime.TokenStream;

/** AST-based Interpreter.
 * This class holds variables to manage the state of a running
 * interpreter, such as symbol tables.
 * It also has a main() method to scan, parse, generate the AST,
 * and then execute that AST.
 */
public class Interpreter {
    // These objects are used to managed the state of the interpreter
    // as it executes a program.
    private Map<String, String> stringVars = new HashMap<>();
    private Map<String, Boolean> boolVars = new HashMap<>();
    private Scanner stdin = new Scanner(System.in);

    // Getters for interpreter state variables
    public Map<String,String> getStringVars() { return stringVars; }
    public Map<String,Boolean> getBoolVars() { return boolVars; }

    public String readInputLine() {
        return stdin.nextLine();
    }

    /** Calls the Tokenizer to extract tokens from the source text file. */
    public static TokenStream getTokens(Path sourceFile) throws IOException {
        return new Tokenizer(
            Interpreter.class.getResourceAsStream("tokenSpec.txt"),
            ParseRules.VOCABULARY
        ).streamFrom(sourceFile);
    }

    /** Calls the ANTLR-generated parser to form the tokens into a parse tree. */
    public static ParseRules.ProgContext parse(TokenStream tokens) {
        ParseRules parser = new ParseRules(tokens);
        Errors.register(parser);
        return parser.prog();
    }

    /** Does scanning, parsing, AST generation, and finally AST execution. */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            Errors.error("need 1 command-line arg: input source file");
        }
        Path sourceFile = Path.of(args[0]);
        TokenStream tokens = getTokens(sourceFile);
        ParseRules.ProgContext ptreeRoot = parse(tokens);
        Stmt astRoot = ASTGen.gen(ptreeRoot);
        astRoot.exec(new Interpreter());
    }
}
