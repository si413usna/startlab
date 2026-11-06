package si413;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

/** An instance of this class represents the state of a running interpreter. */
public class RunState {
    private Scanner stdin = new Scanner(System.in);
    private Frame globalFrame = new Frame();

    public String readLine() {
        return stdin.nextLine();
    }

    public Frame currentFrame() {
        return globalFrame;
    }
}
