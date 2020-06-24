package readability;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    static String wordEnd = "\\s+|\\p{Z}|\\p{Space}|\\p{Blank}";
    static String sentenceEnd = "[.!?]\\s|[.!?]\\p{Blank}|[.!?]\\p{Space}";
    static HashMap<Integer, String> readabilityScores = new HashMap<Integer, String>();

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void main(String[] args) {
        String filePath = args[0];
        File file = new File(filePath);
        String text = "";
        try {
            text = readFileAsString(filePath);
        } catch (IOException e) {
            System.out.println("Cannot read file: " + filePath);
        }
        hashmapReadabilityScoresInit();
        double[] textProperties = analyzeText(text);
        double score = calculateReadabilityScore(textProperties);
        showTextReadability(text, textProperties, score);
    }

    private static void hashmapReadabilityScoresInit() {
        readabilityScores.put(1, "5-6");
        readabilityScores.put(2, "6-7");
        readabilityScores.put(3, "7-9");
        readabilityScores.put(4, "9-10");
        readabilityScores.put(5, "10-11");
        readabilityScores.put(6, "11-12");
        readabilityScores.put(7, "12-13");
        readabilityScores.put(8, "13-14");
        readabilityScores.put(9, "14-15");
        readabilityScores.put(10, "15-16");
        readabilityScores.put(11, "16-17");
        readabilityScores.put(12, "17-18");
        readabilityScores.put(13, "18-24");
        readabilityScores.put(14, "25+");
    }

    private static void showTextReadability(String text, double[] textProperties, double score) {
        System.out.println("The text is:\n" + text + "\n");
        System.out.printf("Words: %.0f\nSentences: %.0f\nCharacters: %.0f\nThe score is: %.2f\n",
                textProperties[0], textProperties[1], textProperties[2], score);
        int scoreRounded = (int) Math.ceil(score);
        String ageRange = readabilityScores.get(scoreRounded);
        System.out.println("This text should be understood by " + ageRange + " year olds.");
    }

    private static double calculateReadabilityScore(double[] textProperties) {
        try {
            return (4.71 * (textProperties[2] / textProperties[0]) +
                    0.5 * (textProperties[0] / textProperties[1]) - 21.43);
        } catch (ArithmeticException e) {
            System.out.println("Division by zero: " + Arrays.toString(textProperties));
        }
        return 0;
    }

    private static double[] analyzeText(String text) {
        String[] sentences = text.split(sentenceEnd);
        double wordCount = 0;
        for (String sentence : sentences) {
            wordCount += analyzeSentence(sentence);
        }
        double charCount = getCharCount(text);
        return new double[]{wordCount, sentences.length, charCount};
    }

    private static int analyzeSentence(String sentence) {
        String[] words = sentence.split(wordEnd);
        return words.length;
    }

    private static int getCharCount(String text) {
        String newText = text.replace(" ", "");
        return newText.length();
    }
}