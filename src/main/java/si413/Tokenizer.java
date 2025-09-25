package si413;

/** This file contains a generic tokenizer based on the tokenSpec.txt file.
 * You should NOT need to change anything here - just change the
 * tokenSpec.txt file for your language as needed.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.function.Predicate;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.misc.Pair;

/** Generic Tokenizer (aka Scanner or Lexer) based on Java regex library.
 * Reads token specifications from a plain text file, by default in
 * main/resources/si413/tokenSpec.txt
 *
 * That file make have comment lines starting with #, or token spec
 * lines of the form
 *   TOKENNAME: regex
 *
 * The regular expressions cannot start or end with whitespace - use
 * character classes like [ ] if you want to start or end your regex
 * with a space.
 *
 * Designed to be compatible with ANTLRv4 parsers. Use the streamFrom()
 * method to get a TokenStream which can be passed to a parser.
 * Be sure to use a tokens { NAME, NAME, ... } block in your parser grammar
 * to match with the names of tokens in tokenSpec.txt.
 */
public class Tokenizer {
    /** Token type used internally to indicate a regex should be skipped. */
    public static final int IGNORE_TYPE = -5;

    /** The internal representation of a single token spec, which has a type and a regex. */
    private static record TokenSpec(int type, Predicate<String> matches) { }

    /** Regex used to parse each line of the tokenSpec.txt file.
     * Distinguishes between blank or comment lines, and valid token spec lines.
     */
    private static Pattern specFileLine = Pattern.compile(
        "^\\s*(?:#.*|(?:(?<name>[A-Z]\\w*)|ignore)\\s*:\\s*(?<pat>.*?)\\s*)$");

    private List<TokenSpec> specs;
    private Set<Integer> initialDisabledTokens;

    /** Loads a tokenizer from the given input spec file and vocabulary of token types.
     * No tokens will be disabled by default.
     */
    public Tokenizer(InputStream specFile, Vocabulary vocab) throws IOException {
        this(specFile, vocab, Set.of());
    }

    /** Loads a tokenizer from a spec file and token type vocabular, with initially disabled tokens. */
    public Tokenizer(InputStream specFile,
                     Vocabulary vocab,
                     Set<Integer> initialDisabledTokens)
            throws IOException
    {
        this.initialDisabledTokens = initialDisabledTokens;

        // extract token types from parser vocabulary
        Map<String, Integer> tokenTypes = new HashMap<>();
        for (int type = 0; type <= vocab.getMaxTokenType(); ++type) {
            String name = vocab.getSymbolicName(type);
            if (name != null) tokenTypes.put(name, type);
        }

        // read specs from spec file
        specs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(specFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher mat = specFileLine.matcher(line);
                if (!mat.matches()) {
                    Errors.error(String.format("Improper token spec line: '%s'", line));
                }
                String pat = mat.group("pat");
                if (pat == null) continue;
                String tokName = mat.group("name");
                int tokType;
                if (tokName == null) {
                    tokType = IGNORE_TYPE;
                } else {
                    tokType = tokenTypes.getOrDefault(tokName, Token.INVALID_TYPE);
                    if (tokType == Token.INVALID_TYPE) {
                        Errors.error(String.format("Token %s not found in vocab", tokName));
                    }
                }
                specs.add(new TokenSpec(tokType, Pattern.compile(pat, Pattern.MULTILINE).asMatchPredicate()));
            }
        }
    }

    /** Produces a stream of tokens from the given input file. */
    public TokenStream streamFrom(Path sourceFile) throws IOException {
        return new BufferedTokenStream(new Tokens(CharStreams.fromPath(sourceFile)));
    }

    /** TokenSource implementation to get tokens from a source file.
     * The most important method is nextToken().
     */
    public class Tokens implements TokenSource {
        private TokenFactory<?> tokenFactory = CommonTokenFactory.DEFAULT;
        private CharStream source;
        private int line = 1;
        private int col = 1;
        private boolean hitEOF = false;
        private Pair<TokenSource,CharStream> sourcePair;
        private Set<Integer> disabledTokens;

        public Tokens(CharStream source) {
            this.source = source;
            this.sourcePair = new Pair<>(this, source);
            this.disabledTokens = new HashSet<>(initialDisabledTokens);
        }

        @Override
        public Token nextToken() {
            // return EOF token if already at the end
            if (hitEOF) return tokenFactory.create(Token.EOF, null);

            StringBuilder tokText = new StringBuilder();
            int type = Token.INVALID_TYPE;
            int startLine = line;
            int startCol = col;
            int mark = source.mark();
            int markIndex = source.index();

            // keep adding characters to tokText unitl transition
            // from matching to non-matching
            while (true) {
                int nextChar = source.LA(1);
                if (nextChar == CharStream.EOF) {
                    hitEOF = true;
                    break;
                }
                int popLength = tokText.length();
                tokText.appendCodePoint(nextChar);
                boolean matched = false;
                for (TokenSpec spec : specs) {
                    if (!disabledTokens.contains(spec.type())
                            && spec.matches().test(tokText.toString()))
                    {
                        matched = true;
                        type = spec.type();
                        break;
                    }
                }
                if (!matched && type != Token.INVALID_TYPE) {
                    // tokenization finished; we previously had a valid token
                    // but no longer
                    tokText.setLength(popLength);
                    break;
                }
                source.consume();
                // update line and column number bookkeeping
                if (nextChar == '\n') {
                    ++line;
                    col = 1;
                }
                else ++col;
            }
            if (tokText.length() == 0 && hitEOF) {
                source.release(mark);
                return tokenFactory.create(Token.EOF, null);
            }
            if (type == Token.INVALID_TYPE) {
                source.seek(markIndex);
                line = startLine;
                col = startCol;
                source.release(mark);
                tokText.setLength(10);
                Errors.syntax(
                    "Tokenizer",
                    getSourceName(),
                    line,
                    col,
                    String.format("invalid token starting with '%s'", tokText.toString()));
            }
            source.release(mark);
            if (type == IGNORE_TYPE) {
                return nextToken(); // skip this match and recurse for the next one
            }
            else {
                assert tokText.length() > 0;
                return tokenFactory.create(
                    sourcePair,
                    type,
                    tokText.toString(),
                    Token.DEFAULT_CHANNEL,
                    0,
                    tokText.length()-1,
                    startLine,
                    startCol
                );
            }
        }

        public void disableToken(int type) {
            disabledTokens.add(type);
        }

        public void enableToken(int type) {
            disabledTokens.remove(type);
        }

        @Override
        public String getSourceName() {
            return source.getSourceName();
        }

        @Override
        public CharStream getInputStream() {
            return source;
        }

        @Override
        public int getLine() {
            return line;
        }

        @Override
        public int getCharPositionInLine() {
            return col;
        }

        @Override
        public TokenFactory<?> getTokenFactory() {
            return this.tokenFactory;
        }

        @Override
        public void setTokenFactory(TokenFactory<?> factory) {
            this.tokenFactory = factory;
        }
    }
}
