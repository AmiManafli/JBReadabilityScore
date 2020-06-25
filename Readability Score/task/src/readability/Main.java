package readability;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    static String wordEnd = "\\s+|\\p{Z}|\\p{Space}|\\p{Blank}";
    static String sentenceEnd = "[.!?]\\s|[.!?]\\p{Blank}|[.!?]\\p{Space}";
    static HashMap<Integer, String> readabilityScores = new HashMap<Integer, String>();

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        String filePath = "dataset_91022.txt";
        String filePath = args[0];

        File file = new File(filePath);
        String text = "";
        try {
            text = readFileAsString(filePath);
        } catch (IOException e) {
            System.out.println("Cannot read file: " + filePath);
        }
        TextStats textStats = new TextStats(text);
        hashmapReadabilityScoresInit();
        analyzeText(textStats);
        showTextStats(textStats);
        System.out.println("Enter the score you want to calculate" +
                "(ARI, FK, SMOG, CL, all):");
        String scoreType = scanner.nextLine();
        calculateScore(scoreType, textStats);
        showReadabilityScores(textStats);
    }

    private static void showReadabilityScores(TextStats textStats) {
        String ageRange = "";
        System.out.println();
        if (textStats.scoreAutomated.isPresent()) {
            System.out.printf("Automated Readability Index: %.2f (about %s year olds).\n",
                    textStats.scoreAutomated.get(), getAgeRange(textStats.scoreAutomated));
        }
        if (textStats.scoreFK.isPresent()) {
            System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds).\n",
                    textStats.scoreFK.get(), getAgeRange(textStats.scoreFK));
        }
        if (textStats.scoreSMOG.isPresent()) {
            System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds).\n",
                    textStats.scoreSMOG.get(), getAgeRange(textStats.scoreSMOG));
        }
        if (textStats.scoreCL.isPresent()) {
            System.out.printf("Coleman–Liau index: %.2f (about %s year olds).\n",
                    textStats.scoreCL.get(), getAgeRange(textStats.scoreCL));
        }
    }

    private static String getAgeRange(Optional<Double> score) {
        return readabilityScores.get((int) Math.round(score.get()));
    }

    private static void calculateScore(String scoreType, TextStats textStats) {
        switch (scoreType) {
            case "ARI":
                textStats.scoreAutomated = Optional.of(automatedReadabilityScore(textStats));
                break;
            case "FK":
                textStats.scoreFK = Optional.of(fleschKincaidReadabilityScore(textStats));
                break;
            case "SMOG":
                textStats.scoreSMOG = Optional.of(SMOGReadabilityScore(textStats));
                break;
            case "CL":
                textStats.scoreCL = Optional.of(colemanLiauReadabilityScore(textStats));
                break;
            case "all":
                textStats.scoreAutomated = Optional.of(automatedReadabilityScore(textStats));
                textStats.scoreFK = Optional.of(fleschKincaidReadabilityScore(textStats));
                textStats.scoreSMOG = Optional.of(SMOGReadabilityScore(textStats));
                textStats.scoreCL = Optional.of(colemanLiauReadabilityScore(textStats));
                break;
            default:
                break;
        }
    }

    private static void showTextStats(TextStats textStats) {
        System.out.println("The text is:\n" + textStats.text + "\n");
        System.out.printf("Words: %d\nSentences: %d\nCharacters: %d\n" +
                        "Syllables: %d\nPolysyllables: %d\n",
                textStats.wordCount, textStats.sentenceCount, textStats.charCount,
                textStats.syllableCount, textStats.polysyllableCount);
    }

    private static void hashmapReadabilityScoresInit() {
        readabilityScores.put(1, "6");
        readabilityScores.put(2, "7");
        readabilityScores.put(3, "9");
        readabilityScores.put(4, "10");
        readabilityScores.put(5, "11");
        readabilityScores.put(6, "12");
        readabilityScores.put(7, "13");
        readabilityScores.put(8, "14");
        readabilityScores.put(9, "15");
        readabilityScores.put(10, "16");
        readabilityScores.put(11, "17");
        readabilityScores.put(12, "18");
        readabilityScores.put(13, "24");
        readabilityScores.put(14, "25+");
    }

    private static double automatedReadabilityScore(TextStats textStats) {
        try {
            return (4.71 * ((double) textStats.charCount / textStats.wordCount) +
                    0.5 * ((double) textStats.wordCount / textStats.sentenceCount) - 21.43);
        } catch (ArithmeticException e) {
            System.out.println("Division by zero");
        }
        return 0;
    }

    private static double colemanLiauReadabilityScore(TextStats textStats) {
        return 0.0588 * ((double) textStats.charCount * 100 / textStats.wordCount) -
                0.296 * ((double) textStats.sentenceCount * 100 / textStats.wordCount) -
                15.8;
    }

    private static double SMOGReadabilityScore(TextStats textStats) {
        return 1.043 * Math.sqrt((double) textStats.polysyllableCount * 30 /
                textStats.sentenceCount) + 3.1291;
    }

    private static double fleschKincaidReadabilityScore(TextStats textStats) {
        return (0.39 * (double) textStats.wordCount / textStats.sentenceCount +
                11.8 * (double) textStats.syllableCount / textStats.wordCount - 15.59);
    }

    private static void analyzeText(TextStats textStats) {
        String[] sentences = textStats.text.split(sentenceEnd);
        for (String sentence : sentences) {
            sentence = sentence.replaceAll("[^a-zA-Z0-9\\s]", "");
            textStats.wordCount += analyzeSentence(sentence, textStats);
        }
        textStats.charCount = getCharCount(textStats);
        textStats.sentenceCount = sentences.length;
    }

    private static int analyzeSentence(String sentence, TextStats textStats) {
        String[] words = sentence.split(wordEnd);
        for (String word : words) {
            textStats.syllableCount += analyzeWord(word, textStats);
        }
        return words.length;
    }

    private static int analyzeWord(String word, TextStats textStats) {
        int syllableCount = 0;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            boolean isSyllable = !(i != 0 && isVowel(word.charAt(i - 1)) ||
                    (i == word.length() - 1 && ch == 'e'));
            if (isVowel(ch) && isSyllable) {
                syllableCount++;
            }
        }
        if (syllableCount > 2) {
            textStats.polysyllableCount++;
        }
        return syllableCount > 0 ? syllableCount : 1;
    }

    private static boolean isVowel(char ch) {
        if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' ||
                ch == 'u' || ch == 'y' || ch == 'A' || ch == 'E' ||
                ch == 'I' || ch == 'O' || ch == 'U' || ch == 'Y') {
            return true;
        }
        return false;
    }

    private static int getCharCount(TextStats textStats) {
        String newText = textStats.text.replace(" ", "");
        return newText.length();
    }
}