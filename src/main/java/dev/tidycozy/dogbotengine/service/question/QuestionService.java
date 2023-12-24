package dev.tidycozy.dogbotengine.service.question;

import dev.tidycozy.dogbotengine.model.PhraseContext;
import dev.tidycozy.dogbotengine.model.question.QuestionType;
import dev.tidycozy.dogbotengine.service.AnswerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService implements AnswerService {

    private Map<String, AnswerService> servicesMap = new HashMap<>();

    private AreYouService areYouService;
    private CanYouService canYouService;
    private DoYouService doYouService;
    private DoYouWhatService doYouWhatService;
    private DoYouWhereService doYouWhereService;
    private HaveYouService haveYouService;
    private HowService howService;
    private IsThereService isThereService;
    private ShouldIService shouldIService;
    private WhereService whereService;

    public QuestionService(AreYouService areYouService,
                           CanYouService canYouService,
                           DoYouService doYouService,
                           DoYouWhatService doYouWhatService,
                           DoYouWhereService doYouWhereService,
                           HaveYouService haveYouService,
                           HowService howService,
                           IsThereService isThereService,
                           ShouldIService shouldIService,
                           WhereService whereService) {
        this.areYouService = areYouService;
        this.canYouService = canYouService;
        this.doYouService = doYouService;
        this.doYouWhatService = doYouWhatService;
        this.doYouWhereService = doYouWhereService;
        this.haveYouService = haveYouService;
        this.howService = howService;
        this.isThereService = isThereService;
        this.shouldIService = shouldIService;
        this.whereService = whereService;

        servicesMap.put(QuestionType.ARE_YOU.name(), areYouService);
        servicesMap.put(QuestionType.CAN_YOU.name(), canYouService);
        servicesMap.put(QuestionType.DO_YOU.name(), doYouService);
        servicesMap.put(QuestionType.DO_YOU.name() + QuestionType.WHAT, doYouWhatService);
        servicesMap.put(QuestionType.DO_YOU.name() + QuestionType.WHERE.name(), doYouWhereService);
        servicesMap.put(QuestionType.HAVE_YOU.name(), haveYouService);
        servicesMap.put(QuestionType.HOW.name(), howService);
        servicesMap.put(QuestionType.IS_THERE.name(), isThereService);
        servicesMap.put(QuestionType.SHOULD_I.name(), shouldIService);
        servicesMap.put(QuestionType.WHERE.name(), whereService);
    }

    @Override
    public String computeAnswer(PhraseContext context) {
        String analysis = "";

        List<QuestionType> questionTypes = QuestionType.getQuestionTypes(context);
        StringBuilder serviceKey = new StringBuilder();
        for (QuestionType questionType : questionTypes) {
            serviceKey.append(questionType.name());
        }
        AnswerService answerService = servicesMap.get(serviceKey.toString());
        System.out.println(answerService.computeAnswer(context));

        for (QuestionType questionType : questionTypes) {
            analysis = analysis.concat("You asked a question " + questionType.getDefinition() + "\n");
        }
        analysis = analysis.concat("More specificaly about:");
        for (int i = 0; i < context.getTokens().length; i++) {
            if (PhraseContext.TOKEN_AVAILABLE.equals(context.getWorkingTokens()[i])) {
                if (context.getTags()[i].equals("NOUN")) {
                    if (i != 0 && context.getTags()[i - 1].equals("PRON")) {
                        analysis = analysis.concat(" " + context.getTokens()[i - 1]);
                        context.getWorkingTokens()[i - 1] = PhraseContext.TOKEN_USED;
                    }
                    analysis = analysis.concat(" " + context.getTokens()[i]);
                    context.getWorkingTokens()[i] = PhraseContext.TOKEN_USED;
                } else if (context.getTags()[i].equals("VERB") || context.getTags()[i].equals("ADJ")) {
                    analysis = analysis.concat(" " + context.getTokens()[i]);
                    context.getWorkingTokens()[i] = PhraseContext.TOKEN_USED;
                }
            }
        }
        analysis = analysis.concat("\nWorking tokens: " + String.join(", ", context.getWorkingTokens()) + "\n");
        return analysis;
    }

}
