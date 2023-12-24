package dev.tidycozy.dogbotengine.service;

import dev.tidycozy.dogbotengine.model.PhraseContext;
import dev.tidycozy.dogbotengine.model.question.QuestionType;
import org.springframework.stereotype.Service;

@Service
public class QuestionService implements AnswerService {

    @Override
    public String computeAnswer(PhraseContext context) {
        String analysis = "";
        for (QuestionType questionType : QuestionType.getQuestionTypes(context)) {
            analysis = analysis.concat("You asked a question " + questionType.getDefinition() + "\n");
        }
        analysis = analysis.concat("More specificaly about:");
        for (int i = 0; i < context.getTokens().length; i++) {
            if ("A".equals(context.getWorkingTokens()[i])) {
                if (context.getTags()[i].equals("NOUN")) {
                    if (i != 0 && context.getTags()[i - 1].equals("PRON")) {
                        analysis = analysis.concat(" " + context.getTokens()[i - 1]);
                        context.getWorkingTokens()[i] = "X";
                    }
                    analysis = analysis.concat(" " + context.getTokens()[i]);
                    context.getWorkingTokens()[i] = "X";
                } else if (context.getTags()[i].equals("VERB") || context.getTags()[i].equals("ADJ")) {
                    analysis = analysis.concat(" " + context.getTokens()[i]);
                    context.getWorkingTokens()[i] = "X";
                }
            }
        }
        analysis = analysis.concat("\nWorking tokens: " + String.join(", ", context.getWorkingTokens()) + "\n");
        return analysis;
    }
}
