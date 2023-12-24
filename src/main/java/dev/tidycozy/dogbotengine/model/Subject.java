package dev.tidycozy.dogbotengine.model;

public record Subject(String name) {

    public static Subject I = new Subject("I");
    public static Subject YOU = new Subject("You");
    public static Subject HE = new Subject("He");
    public static Subject SHE = new Subject("She");
    public static Subject WE = new Subject("We");
    public static Subject THEY = new Subject("They");
    public static Subject SOMEBODY = new Subject("Somebody");
    public static Subject SOMETHING = new Subject("Something");
    public static Subject UNKNOW_SUBJECT = new Subject("Unknown subject");
    public static Subject NO_SUBJECT = new Subject("No subject"); // Used in NLPService

    public static Subject getFromString(String string) {
        return switch (string.toLowerCase()) {
            case "i", "me" -> I;
            case "you" -> YOU;
            case "he", "him" -> HE;
            case "she", "her" -> SHE;
            case "we", "us" -> WE;
            case "they", "them" -> THEY;
            case "anyone" -> SOMEBODY;
            case "it" -> SOMETHING;
            default -> UNKNOW_SUBJECT;
        };
    }

}
