package dev.tidycozy.dogbotengine.model;

public enum Punctuation {

    STATEMENT,

    QUESTION,

    EXCLAMATION,

    UNKNOWN;

    public static Punctuation getFromString(String string) {
        return switch (string) {
            case "." -> STATEMENT;
            case "?" -> QUESTION;
            case "!" -> EXCLAMATION;
            default -> UNKNOWN;
        };
    }

}
