package dev.tidycozy.dogbotengine.model;

public enum Punctuation {

    STATEMENT,

    QUESTION,

    EXCLAMATION,

    UNKNOWN;

    public static Punctuation getFromCharacter(String character) {
        return switch (character) {
            case "." -> STATEMENT;
            case "?" -> QUESTION;
            case "!" -> EXCLAMATION;
            default -> UNKNOWN;
        };
    }

}
