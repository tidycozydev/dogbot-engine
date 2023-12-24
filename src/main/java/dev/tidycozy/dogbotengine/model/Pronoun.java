package dev.tidycozy.dogbotengine.model;

public enum Pronoun {

    MY("your"),
    YOUR("my");

    private final String opposite;

    Pronoun(String opposite) {
        this.opposite = opposite;
    }

    public Pronoun getFromText(String text) {
        if (text.equals("my")) {
            return MY;
        }
        return YOUR;
    }

    public String getOpposite() {
        return opposite;
    }
}
