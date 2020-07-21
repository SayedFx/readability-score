package readability;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StageFourTextDifficultyAnalyser {
    private static final double[] ages = {0, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24};
    private static TextStats textStats;

    public static void printWelcome(String text) {
        textStats = new TextStats(text);
        printScore();
    }


    private static void printScore() {
        System.out.printf("Words: %.0f\n" +
                        "Sentences: %.0f\n" +
                        "Characters: %.0f\n" +
                        "Syllables: %.2f\n" +
                        "Polysyllables: %.0f\n" +
                        "Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ",
                textStats.wordCount,
                textStats.sentenceCount,
                textStats.characterCount,
                textStats.syllableCount,
                textStats.polysyllableWordCount);
        Scanner scanner = new Scanner(System.in);

                double ariAge = getAge(textStats.automatedReadabilityIndex);
                double fkAge = getAge(textStats.fleschKencaidScore);
                double smogAge = getAge(textStats.sMOG);
                double clAge = getAge(textStats.colemanLiau);
        switch (scanner.next()) {
            case "all":
                System.out.printf("\nAutomated Readability Index: %.2f (about %.0f year olds).\n" +
                                "Flesch–Kincaid readability tests: %.2f  (about %.0f year olds).\n" +
                                "Simple Measure of Gobbledygook: %.2f  (about %.0f year olds).\n" +
                                "Coleman–Liau index: %.2f (about %.0f year olds).\n" +
                                " \n" +
                                "This text should be understood in average by %.2f year olds.",
                        textStats.automatedReadabilityIndex,
                        ariAge,
                        textStats.fleschKencaidScore,
                        fkAge,
                        textStats.sMOG,
                        smogAge,
                        textStats.colemanLiau,
                        clAge,
                        (ariAge + fkAge + smogAge + clAge) / 4.0);
                break;
            case "ARI":
                System.out.printf("\nAutomated Readability Index: %.2f (about %.0f year olds).\n" +
                                " \n" +
                                "This text should be understood in average by %.2f year olds.",
                        textStats.automatedReadabilityIndex,
                        ariAge,
                        ariAge);
                break;
            case "FK":
                System.out.printf("\n" +
                                "Flesch–Kincaid readability tests: %.2f  (about %.0f year olds).\n" +
                                " \n" +
                                "This text should be understood in average by %.2f year olds.",
                        textStats.fleschKencaidScore,
                        fkAge,
                        fkAge);
                break;
            case "SMOG":
                System.out.printf("\n" +
                                "Simple Measure of Gobbledygook: %.2f  (about %.0f year olds).\n" +
                                " \n" +
                                "This text should be understood in average by %.2f year olds.",
                        textStats.sMOG,
                        smogAge,
                        smogAge);
                break;
            case "CL":
                System.out.printf("\n" +
                                "Coleman–Liau index: %.2f (about %.0f year olds).\n" +
                                " \n" +
                                "This text should be understood in average by %.2f year olds.",
                        textStats.colemanLiau,
                        clAge,
                        clAge);
                break;
        }
    }

    private static Double getAge(double index) {
        return ages[Math.min((int) Math.ceil(index),14)];
    }


    private static class TextStats {
        private final double characterCount;
        private final double sentenceCount;
        private final double wordCount;
        private final double syllableCount;
        private final double polysyllableWordCount;
        private final double fleschKencaidScore;
        private final double sMOG;
        private final double colemanLiau;
        private final double automatedReadabilityIndex;

        public TextStats(String text) {
            characterCount = getCharacterCount(text);
            wordCount = getWordCount(text);
            sentenceCount = getSentenceCount(text);
            List<String> tokenList = getSyllableFormattedWords(text);
            syllableCount = getTotalSyllableCount(tokenList);
            polysyllableWordCount = getPolySyllableWordCount(tokenList);

            automatedReadabilityIndex = (4.71 * (characterCount / wordCount)) + (0.5 * (wordCount / sentenceCount)) - 21.43;
            fleschKencaidScore = (0.39 * wordCount / sentenceCount) + (11.8 * syllableCount / wordCount) - 15.59;
            sMOG = (1.043 * Math.sqrt(polysyllableWordCount * 30 / sentenceCount)) + 3.1291;
            double L = characterCount / (wordCount / 100);
            double S = sentenceCount / (wordCount / 100);
            colemanLiau = (0.0588 * L) - (0.296 * S) - (15.8);

        }


        private long getPolySyllableWordCount(List<String> tokenList) {
            return tokenList.stream().mapToInt(s -> Math.max(s.length() - s.replaceAll("[aeiouy]", "").length(), 1)).filter(i -> i > 2).count();
        }

        private int getTotalSyllableCount(List<String> tokenList) {
            return tokenList.stream().mapToInt(s -> Math.max(s.length() - s.replaceAll("[aeiouy]", "").length(), 1)).sum();
        }

        private List<String> getSyllableFormattedWords(String text) {
            String[] tokens = text.trim().replaceAll("\\s+", " ")
                    .split(" ");
            return Arrays.stream(tokens).map(s -> s.replaceAll("[Ee]$", "")
                    .replaceAll("[Ee]([.?!:])", "$1").toLowerCase()
                    .replaceAll("([aeiouy])[aeiouy]+", "$1")).collect(Collectors.toList());
        }

        private double getSentenceCount(String string) {
            Matcher matcher = Pattern.compile("([!?.].)").matcher(string);
            int sentenceCount = 1;
            while ((matcher.find()))
                sentenceCount++;

            return sentenceCount;
        }


        private double getCharacterCount(String string) {
            return string.replaceAll("[\\s\\n\\t\\r]", "").length();
        }

        private double getWordCount(String string) {
            return string.split("\\s+|[\n\t\r]]").length;
        }

    }

}
