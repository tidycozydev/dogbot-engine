package dev.tidycozy.dogbotengine.service.question;

import dev.tidycozy.dogbotengine.model.PhraseContext;
import dev.tidycozy.dogbotengine.service.AnswerService;
import org.springframework.stereotype.Service;

@Service
public class DoYouService implements AnswerService {

    @Override
    public String computeAnswer(PhraseContext context) {
        return verbNounStructure(context);
    }

    private String verbNounStructure(PhraseContext context) {
        String verb = null;
        String noun = null;

        for (int i = 0; i < context.getTokens().length; i++) {
            if (PhraseContext.TOKEN_AVAILABLE.equals(context.getWorkingTokens()[i])) {
                if (context.getTags()[i].equals("VERB")) {
                    verb = context.getTokens()[i];
                    context.getWorkingTokens()[i] = PhraseContext.TOKEN_USED;
                }
                if (context.getTags()[i].equals("NOUN")) {
                    noun = context.getTokens()[i];
                    context.getWorkingTokens()[i] = PhraseContext.TOKEN_USED;
                }
            }
        }

        return "I do " + verb + " " + noun;
    }
}
