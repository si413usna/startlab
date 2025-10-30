package si413;

import java.nio.file.Path;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/** AST-based Compiler.
 * This class holds variables to manage the state of a running compiler.
 * It also has a main() to scan, parse, generate the AST, and then compile
 * that AST.
 */
public class Compiler {
    // These objects are used to manage the state of the compiler
    // as it goes through the AST.
    private PrintWriter dest;
    private int nextRegNum = 1;
    private List<String> literals = new ArrayList<>();

    /** Returns the open writer to the destination .ll file. */
    public PrintWriter dest() { return dest; }

    /** Creates a new register name and returns it.
     * @return First "%reg1", then "%reg2", etc.
     */
    public String nextRegister() {
        return String.format("%%reg%d", nextRegNum++);
    }

    /** Adds a new string to the list of literals.
     * @return The literal name, like @lit1, @lit2, etc.
     */
    public String addStringLit(String str) {
        literals.add(str);
        return String.format("@lit%d", literals.size());
    }

    /** Constructor for the Compiler object given the output ll file. */
    public Compiler(Path destFile) throws IOException {
        dest = new PrintWriter(destFile.toFile());
    }

    /** Performs the complete compilation for the program given by its AST. */
    public void compile(Stmt astRoot) throws IOException {
        // copy contents of preamble.ll in the resources directory
        try (BufferedReader preamble = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("preamble.ll"))))
        {
            while (true) {
                String line = preamble.readLine();
                if (line == null) break;
                dest.println(line);
            }
        }

        dest.println("define i32 @main() {");

        // call the AST root node compile method to fill in
        // the contents of main()
        astRoot.compile(this);

        dest.println("  ret i32 0");
        dest.println("}");

        // output the string literal definitions
        int litNum = 0;
        for (String str : literals) {
            dest.format("@lit%d = constant [%d x i8] c\"", ++litNum, str.length()+1);
            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                int cval = (int)c;
                if (c != '"' && c != '\\' && cval >= 0x20 && cval <= 0x7e)
                    dest.print(c);
                else
                    dest.format("\\%02X", cval);
            }
            dest.println("\\00\"");
        }

        dest.close();
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

    /** Does scanning, parsing, AST generation, and finally code generation. */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            Errors.error("need 2 command-line arg: source file and output file");
        }
        Path sourceFile = Path.of(args[0]);
        TokenStream tokens = getTokens(sourceFile);
        ParseRules.ProgContext ptreeRoot = parse(tokens);
        Stmt astRoot = ASTGen.gen(ptreeRoot);
        new Compiler(Path.of(args[1])).compile(astRoot);
    }
}
