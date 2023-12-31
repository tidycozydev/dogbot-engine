package dev.tidycozy.dogbotengine.service.statement;

import dev.tidycozy.dogbotengine.model.PhraseContext;
import dev.tidycozy.dogbotengine.service.AnswerService;
import org.springframework.stereotype.Service;

@Service
public class StatementService implements AnswerService {

    @Override
    public String computeAnswer(PhraseContext context) {
        return "You made a statement about: ";
    }
}
