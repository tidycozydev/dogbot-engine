package dev.tidycozy.dogbotengine.service.question;

import dev.tidycozy.dogbotengine.model.PhraseContext;
import dev.tidycozy.dogbotengine.service.AnswerService;
import org.springframework.stereotype.Service;

@Service
public class CanYouService implements AnswerService {
    @Override
    public String computeAnswer(PhraseContext context) {
        return null;
    }
}