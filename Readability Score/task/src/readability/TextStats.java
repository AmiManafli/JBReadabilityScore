package readability;

import java.util.Optional;
import java.util.OptionalDouble;

public class TextStats {
    public String text;
    public int sentenceCount;
    public int wordCount;
    public int charCount;
    public int syllableCount;
    public int polysyllableCount;

    public Optional<Double> scoreAutomated = Optional.empty();
    public Optional<Double> scoreFK = Optional.empty();
    public Optional<Double> scoreSMOG = Optional.empty();
    public Optional<Double> scoreCL = Optional.empty();

    public TextStats(String text) {
        this.text = text;
    }
}
