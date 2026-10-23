package si413;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

/** An instance of this class represents the state of a running interpreter. */
public record RunState(
    Map<String, String> strVars,
    Map<String, Boolean> boolVars,
    Scanner stdin
) {
    public static RunState create() {
        return new RunState(new HashMap<>(), new HashMap<>(), new Scanner(System.in));
    }
}
