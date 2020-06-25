package readability;

public class TextStats {
    public String text;
    public static int sentenceCount;
    public static int wordCount;
    public static int charCount;
    public static int syllablesCount;

    public TextStats(String text) {
        this.text = text;
    }
}
