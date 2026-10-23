package si413;

import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;

/** An instance of this class represents the state of the compiler. */
public class CompileState {
    private PrintWriter out;
    private int registerCount = 0;
    private List<String> postlude = new java.util.ArrayList<>();

    /** Initializes the compier state based on the destination filename. */
    public CompileState(String ofname) throws IOException {
        this.out = new PrintWriter(new java.io.FileWriter(ofname), true);
    }

    /** Getter for the destination output stream. */
    public PrintWriter out() { return out; }

    /** Produces a new register name to use. */
    public String freshReg() {
        return String.format("%%reg%d", ++registerCount);
    }

    /** Adds a new string literal and returns its name. */
    public String addStringLit(String contents) {
        String litName = String.format("@lit%d", ++registerCount);
        StringBuilder line = new StringBuilder();
        line.append(litName);
        line.append(" = constant [");
        line.append(contents.length() + 1);
        line.append(" x i8] c\"");
        for (int i = 0; i < contents.length(); ++i) {
            char c = contents.charAt(i);
            int cval = (int)c;
            if (c != '"' && c != '\\' && cval >= 0x20 && cval <= 0x7e)
                line.append(c);
            else
                line.append(String.format("\\%02X", cval));
        }
        line.append("\\00\"");
        postlude.add(line.toString());
        return litName;
    }

    /** Starts the destination .ll output based on preamble.ll. */
    public void start() throws IOException {
        // dump contents of preamble, if it exists
        var preamb = Main.class.getResourceAsStream("preamble.ll");
        if (preamb != null) {
            var pread = new java.io.BufferedReader(new java.io.InputStreamReader(preamb));
            while (true) {
                String pline = pread.readLine();
                if (pline == null) break;
                out.println(pline);
            }
            pread.close();
        }
    }

    /** Finishes compilation and closes the destination file. */
    public void finish() {
        for (String line : postlude) {
            out.println(line);
        }
        out.close();
    }

}
