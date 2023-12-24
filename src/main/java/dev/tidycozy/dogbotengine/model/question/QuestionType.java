package dev.tidycozy.dogbotengine.model.question;

import dev.tidycozy.dogbotengine.model.PhraseContext;

import java.util.ArrayList;
import java.util.List;

public enum QuestionType {

    ARE_YOU("about if I am"),
    CAN_YOU("about if I can"),
    DO_YOU_KNOW("about if I know"),
    DO_YOU_LIKE("about if I like"),
    DO_YOU_THINK("about if I think"),
    HAVE_YOU("about if I have"),
    IS_THERE("about presence"),
    SHOULD_I("about choice"),
    WHO("about someone"),
    WHAT("about something"),
    WHERE("about a place"),
    WHEN("about a time"),
    WHY("about a reason"),
    WHICH("about a choice"),
    WHOSE("about a possession"),
    HOW("about a method or a way"),
    UNKNOWN("UNKNOW QUESTION TYPE");

    private final String definition;

    QuestionType(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public static List<QuestionType> getQuestionTypes(PhraseContext context) {
        List<QuestionType> questionTypes = new ArrayList<>();

        // Global testing on sentence
        checkQuestionType(context, questionTypes, ARE_YOU, "are", "you");
        checkQuestionType(context, questionTypes, CAN_YOU, "can", "you");
        checkQuestionType(context, questionTypes, DO_YOU_KNOW, "do", "you", "know");
        checkQuestionType(context, questionTypes, DO_YOU_LIKE, "do", "you", "like");
        checkQuestionType(context, questionTypes, DO_YOU_THINK, "do", "you", "think");
        checkQuestionType(context, questionTypes, HAVE_YOU, "have", "you");
        checkQuestionType(context, questionTypes, IS_THERE, "is", "there");
        checkQuestionType(context, questionTypes, SHOULD_I, "should", "i");

        for (int i = 0; i < context.getTags().length; i++) {
            String tag = context.getTags()[i];
            String tagForLemmas = context.getTagsForLemmas()[i];

            // We want to test only the tokens that can be relevant
            if (tag.contains("ADV") || tagForLemmas.contains("WP") || tagForLemmas.contains("WRB")) {
                checkQuestionType(context, questionTypes, WHO, "who");
                checkQuestionType(context, questionTypes, WHAT, "what");
                checkQuestionType(context, questionTypes, WHERE, "where");
                checkQuestionType(context, questionTypes, WHEN, "when");
                checkQuestionType(context, questionTypes, WHY, "why");
                checkQuestionType(context, questionTypes, WHICH, "which");
                checkQuestionType(context, questionTypes, WHOSE, "whose");
                checkQuestionType(context, questionTypes, HOW, "how");
            }
        }

        // Unknown question type
        if (questionTypes.isEmpty()) {
            questionTypes.add(UNKNOWN);
        }

        return questionTypes;
    }

    private static void checkQuestionType(PhraseContext context,
                                          List<QuestionType>questionTypes,
                                          QuestionType questionType,
                                          String... questionParts) {
        String[] tokens = context.getTokens();
        String[] workingTokens = context.getWorkingTokens();

        String firstPart = questionParts[0];

        for (int i = 0; i < tokens.length; i++) {

            // If the first part is a match, we check if the following parts are right after
            if (tokens[i].toLowerCase().contains(firstPart)
                    && PhraseContext.TOKEN_AVAILABLE.equals(workingTokens[i])) {
                boolean questionMatched = true;

                // Avoid IndexOutOfBounds on tokens
                if ((i + questionParts.length - 1) <= tokens.length) {
                    int subIteration = 1;
                    for (int j = 1; j < questionParts.length; j++) {
                        // The next token is not the good one, or it has already been used (working token already checked)
                        if (!tokens[i + subIteration].toLowerCase().contains(questionParts[j])
                                || PhraseContext.TOKEN_USED.equals(workingTokens[i])) {
                            questionMatched = false;
                            break;
                        }
                        subIteration++;
                    }
                } else {
                    questionMatched = false;
                }

                // We found all the parts of the question
                if (questionMatched) {
                    // We update the workings tokens to mark them as already used
                    for (int j = i; j < i + questionParts.length; j++) {
                        workingTokens[j] = PhraseContext.TOKEN_USED;
                    }

                    // We add the question type
                    questionTypes.add(questionType);
                }
            }
        }
    }

}
