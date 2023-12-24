package dev.tidycozy.dogbotengine.service;

import dev.tidycozy.dogbotengine.model.PhraseContext;

public interface AnswerService {

    String computeAnswer(PhraseContext context);

}
