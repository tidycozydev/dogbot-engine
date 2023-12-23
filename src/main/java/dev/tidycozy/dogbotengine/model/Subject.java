package dev.tidycozy.dogbotengine.model;

public class Subject {

    public static Subject I = new Subject("I");
    public static Subject YOU = new Subject("You");
    public static Subject HE = new Subject("He");
    public static Subject SHE = new Subject("She");
    public static Subject WE = new Subject("We");
    public static Subject THEY = new Subject("They");
    public static Subject SOMETHING = new Subject("Something");
    public static Subject UNKNOW_SUBJECT = new Subject("Unknown subject");
    public static Subject NO_SUBJECT = new Subject("No subject"); // Used in NLPService

    private String name;

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Subject getFromString(String string) {
        return switch (string.toLowerCase()) {
            case "i" -> I;
            case "you" -> YOU;
            case "he" -> HE;
            case "she" -> SHE;
            case "we" -> WE;
            case "they" -> THEY;
            case "it" -> SOMETHING;
            default -> UNKNOW_SUBJECT;
        };
    }

}
