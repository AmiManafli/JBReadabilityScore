package readability;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static String wordEnd = "\\s+|\\p{Z}|\\p{Space}|\\p{Blank}";
    static String sentenceEnd = "[.!?]\\s|[.!?]\\p{Blank}|[.!?]\\p{Space}";
    static HashMap<Integer, String> readabilityScores = new HashMap<Integer, String>();

    static int sentenceCount;
    static int wordCount;
    static int charCount;
    static int syllableCount;
    static int polysyllableCount;

    static double scoreAutomated;
    static double scoreFK;
    static double scoreSMOG;
    static double scoreCL;

    enum ScoreShow {
        ARI, FK, SMOG, CL, ALL,
    }

    private static ScoreShow score;

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filePath = args[0];
        File file = new File(filePath);
        String text = "";
        try {
            text = readFileAsString(filePath);
        } catch (IOException e) {
            System.out.println("Cannot read file: " + filePath);
        }
        hashmapReadabilityScoresInit();
        analyzeText(text);
        showTextStats(text);
        System.out.println("Enter the score you want to calculate" +
                "(ARI, FK, SMOG, CL, all):");
        String scoreType = scanner.nextLine();
        calculateScore(scoreType);
        showReadabilityScores();
    }

    private static void showReadabilityScores() {
        String ageRange = "";
        System.out.println();
        if (score == ScoreShow.ARI || score == ScoreShow.ALL) {
            ageRange = readabilityScores.get((int) Math.round(scoreAutomated));
            System.out.printf("Automated Readability Index: %.2f (about %s year olds).\n",
                    scoreAutomated, ageRange);
        }
        if (score == ScoreShow.FK || score == ScoreShow.ALL) {
            ageRange = readabilityScores.get((int) Math.round(scoreFK));
            System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds).\n",
                    scoreFK, ageRange);
        }
        if (score == ScoreShow.SMOG || score == ScoreShow.ALL) {
            ageRange = readabilityScores.get((int) Math.round(scoreSMOG));
            System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds).\n",
                    scoreSMOG, ageRange);
        }
        if (score == ScoreShow.CL || score == ScoreShow.ALL) {
            ageRange = readabilityScores.get((int) Math.round(scoreCL));
            System.out.printf("Coleman–Liau index: %.2f (about %s year olds).\n",
                    scoreCL, ageRange);
        }
    }

    private static void calculateScore(String scoreType) {
        switch (scoreType) {
            case "ARI":
                scoreAutomated = automatedReadabilityScore();
                score = ScoreShow.ARI;
                break;
            case "FK":
                scoreFK = fleschKincaidReadabilityScore();
                score = ScoreShow.FK;
                break;
            case "SMOG":
                scoreSMOG = SMOGReadabilityScore();
                score = ScoreShow.SMOG;
                break;
            case "CL":
                scoreCL = colemanLiauReadabilityScore();
                score = ScoreShow.CL;
                break;
            case "all":
                scoreAutomated = automatedReadabilityScore();
                scoreFK = fleschKincaidReadabilityScore();
                scoreSMOG = SMOGReadabilityScore();
                scoreCL = colemanLiauReadabilityScore();
                score = ScoreShow.ALL;
                break;
            default:
                break;
        }
    }

    private static void showTextStats(String text) {
        System.out.println("The text is:\n" + text + "\n");
        System.out.printf("Words: %d\nSentences: %d\nCharacters: %d\n" +
                        "Syllables: %d\nPolysyllables: %d\n",
                wordCount, sentenceCount, charCount, syllableCount, polysyllableCount);
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

    private static double automatedReadabilityScore() {
        try {
            return (4.71 * ((double) charCount / wordCount) +
                    0.5 * ((double) wordCount / sentenceCount) - 21.43);
        } catch (ArithmeticException e) {
            System.out.println("Division by zero");
        }
        return 0;
    }

    private static double colemanLiauReadabilityScore() {
        return 0.0588 * ((double) charCount * 100 / wordCount) - 0.296 * ((double) sentenceCount * 100 / wordCount) -
                15.8;
    }

    private static double SMOGReadabilityScore() {
        return 1.043 * Math.sqrt((double) polysyllableCount * 30 / sentenceCount)
                + 3.1291;
    }

    private static double fleschKincaidReadabilityScore() {
        return (0.39 * (double) wordCount / sentenceCount +
                11.8 * (double) syllableCount / wordCount - 15.59);
    }

    private static void analyzeText(String text) {
        String[] sentences = text.split(sentenceEnd);
        for (String sentence : sentences) {
            sentence = sentence.replaceAll("[^a-zA-Z0-9\\s]", "");
            wordCount += analyzeSentence(sentence);
        }
        charCount = getCharCount(text);
        sentenceCount = sentences.length;
    }

    private static int analyzeSentence(String sentence) {
        String[] words = sentence.split(wordEnd);
        for (String word : words) {
            syllableCount += analyzeWord(word);
        }
        return words.length;
    }

    private static int analyzeWord(String word) {
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
            polysyllableCount++;
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

    private static int getCharCount(String text) {
        String newText = text.replace(" ", "");
        return newText.length();
    }
}