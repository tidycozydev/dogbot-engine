package dev.tidycozy.dogbotengine.model;

import java.util.Arrays;
import java.util.List;

public class PhraseContext {

    private final String sentence;

    private final String[] tokens;

    private final String[] workingTokens;

    private final String[] tags;

    private final String[] tagsForLemmas;

    private final Punctuation punctuation;

    private final List<Subject> subjects;

    public static final String TOKEN_AVAILABLE = "A";
    public static final String TOKEN_USED = "X";

    public PhraseContext(String sentence, String[] tokens, String[] tags, String[] tagsForLemmas, Punctuation punctuation, List<Subject> subjects) {
        this.sentence = sentence;
        this.tokens = tokens;
        this.workingTokens = new String[tokens.length];
        Arrays.fill(workingTokens, "A"); // A for available
        this.tags = tags;
        this.tagsForLemmas = tagsForLemmas;
        this.punctuation = punctuation;
        this.subjects = subjects;
    }

    public String getSentence() {
        return sentence;
    }

    public String[] getTokens() {
        return tokens;
    }

    public String[] getWorkingTokens() {
        return workingTokens;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] getTagsForLemmas() {
        return tagsForLemmas;
    }

    public Punctuation getPunctuation() {
        return punctuation;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }
}
