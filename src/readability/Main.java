package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        StageFourTextDifficultyAnalyser.printWelcome(Files.readString(Path.of(args[0])));

    }
}
