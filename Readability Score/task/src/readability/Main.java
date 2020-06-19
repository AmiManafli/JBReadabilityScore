package readability;
import java.util.Scanner;

public class Main {
    static String wordEnd = "\\s+|\\p{Z}|\\p{Space}|\\p{Blank}";
    static String sentenceEnd = "[.!?]\\s|[.!?]\\p{Blank}|[.!?]\\p{Space}";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        double wordAvg = analyzeText(text);
        if (wordAvg > 10) {
            System.out.println("HARD");
        } else {
            System.out.println("EASY");
        }
    }

    private static double analyzeText(String text) {
        String[] sentences = text.split(sentenceEnd);
        double wordCount = 0;
        for (String sentence : sentences) {
            wordCount += analyzeSentence(sentence);
        }
//        System.out.printf("The total number of words is %d in %d sentences\n",
//                            wordCount, sentences.length);
        return wordCount / sentences.length;
    }

    private static int analyzeSentence(String sentence) {
        String[] words = sentence.split(wordEnd);
        return words.length;
    }
}
