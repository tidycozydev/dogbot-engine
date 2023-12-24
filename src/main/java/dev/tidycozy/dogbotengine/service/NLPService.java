package dev.tidycozy.dogbotengine.service;

import dev.tidycozy.dogbotengine.model.Punctuation;
import dev.tidycozy.dogbotengine.model.PhraseContext;
import dev.tidycozy.dogbotengine.model.Subject;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class NLPService {

    private QuestionService questionService;

    private StatementService statementService;

    private SentenceDetectorME sentenceDetectorME;

    private TokenizerME tokenizerME;

    private POSTaggerME posTaggerME;

    private POSTaggerME posTaggerMEForLemmatizer;

    private DictionaryLemmatizer dictionaryLemmatizer;

    private ChunkerME chunkerME;

    public NLPService(QuestionService questionService, StatementService statementService) {
        this.questionService = questionService;
        this.statementService = statementService;

        initSentenceDetectorME();
        initTokenizerME();
        initPOSTaggerME();
        initPOSTaggerMEForLemmatizer();
        initDictionaryLemmatizer();
        initChunkerME();
    }

    private void initSentenceDetectorME() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin");
            SentenceModel model = new SentenceModel(inputStream);
            sentenceDetectorME = new SentenceDetectorME(model);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initTokenizerME() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin");
            TokenizerModel model = new TokenizerModel(inputStream);
            tokenizerME = new TokenizerME(model);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initPOSTaggerME() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin");
            POSModel posModel = new POSModel(inputStream);
            posTaggerME = new POSTaggerME(posModel);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initPOSTaggerMEForLemmatizer() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/en-pos-maxent.bin");
            POSModel posModel = new POSModel(inputStream);
            posTaggerMEForLemmatizer = new POSTaggerME(posModel);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initDictionaryLemmatizer() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/en-lemmatizer.dict");
            dictionaryLemmatizer = new DictionaryLemmatizer(inputStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initChunkerME() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/en-chunker.bin");
            ChunkerModel chunkerModel = new ChunkerModel(inputStream);
            chunkerME = new ChunkerME(chunkerModel);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getDogbotAnswer(String request) {
        String[] phrases = sentenceDetectorME.sentDetect(request);

        for (String phrase : phrases) {
            String[] tokens = tokenizerME.tokenize(phrase);
            String[] tags = posTaggerME.tag(tokens);
            String[] tagsForLemmas = posTaggerMEForLemmatizer.tag(tokens);
            String[] lemmas = dictionaryLemmatizer.lemmatize(tokens, tagsForLemmas);
            String[] chunks = chunkerME.chunk(tokens, tagsForLemmas);
            String[] chunksAsText = findChunksAsText(tokens, tagsForLemmas);

            Punctuation punctuation = findPunctuation(tokens, tags);

            List<Subject> subjects = findSubjects(tokens, tags, tagsForLemmas);

            System.out.println(
                    getDogbotAnswerBySentence(new PhraseContext(phrase, tokens, tags, tagsForLemmas, punctuation, subjects)));

            // Big print to see what's going on with OpenMLP
            debug(phrase, tokens, tags, tagsForLemmas, lemmas, chunks, chunksAsText, punctuation, subjects);
        }

        return "I'm dogbot!";
    }

    private String[] findChunksAsText(String[] tokens, String[] tags) {
        Span[] spans = chunkerME.chunkAsSpans(tokens, tags);
        String[] chunksAsText = new String[spans.length];
        int index = 0;
        for (Span span : spans) {
            String chunkAsText = "";
            for (int i = span.getStart(); i < span.getEnd(); i++) {
                chunkAsText = chunkAsText.concat(tokens[i] + " ");
            }
            chunksAsText[index++] = chunkAsText.trim();
        }
        return chunksAsText;
    }

    private Punctuation findPunctuation(String[] tokens, String[] tags) {
        return "PUNCT".equals(tags[tags.length - 1]) ?
                Punctuation.getFromString(tokens[tokens.length - 1]) :
                Punctuation.UNKNOWN;  // Default will be UNKNOWN
    }

    private List<Subject> findSubjects(String[] tokens, String[] tags, String[] tagsForLemmas) {
        List<Subject> subjectList = new ArrayList<>();
        for (int i = 0; i < tagsForLemmas.length; i++) {
            // Subject pronouns
            if (tagsForLemmas[i].equals("PRP") ||
                    (tagsForLemmas[i].equals("NN") && tags[i].equals("PRON"))) {
                subjectList.add(Subject.getFromString(tokens[i]));
                continue;
            }
            // People
            if (tagsForLemmas[i].equals("NNP") && tags[i].equals("PROPN")) {
                subjectList.add(new Subject(tokens[i]));
            }
        }
        if (subjectList.isEmpty()) {
            subjectList.add(Subject.NO_SUBJECT); // Default will be NO_SUBJECT
        }
        return subjectList;
    }

    private String getDogbotAnswerBySentence(PhraseContext phraseContext) {
        if (phraseContext.getPunctuation().equals(Punctuation.QUESTION)) {
            return questionService.computeAnswer(phraseContext);
        }

        return statementService.computeAnswer(phraseContext);
    }

    private void debug(String sentence,
                       String[] tokens,
                       String[] tags,
                       String[] tagsForLemmas,
                       String[] lemmas,
                       String[] chunks,
                       String[] chunksAsText,
                       Punctuation punctuation,
                       List<Subject> subjects) {
        System.out.println("---SENTENCE: " + sentence);
        System.out.println("-----TOKENS: " + String.join(", ", tokens));
        System.out.println("-------TAGS: " + String.join(", ", tags));
        System.out.println("TAGS4LEMMAS: " + String.join(", ", tagsForLemmas));
        System.out.println("-----LEMMAS: " + String.join(", ", lemmas));
        System.out.println("-----CHUNKS: " + String.join(", ", chunks));
        System.out.println("CHUNKSASTXT: " + String.join(", ", chunksAsText));
        System.out.println("PUNCTUATION: " + punctuation);

        String subjectsAsString = "";
        if (!subjects.isEmpty()) {
            subjectsAsString = subjectsAsString.concat(subjects.get(0).name());
            if (subjects.size() > 1) {
                for (int i = 1; i < subjects.size(); i++) {
                    subjectsAsString = subjectsAsString.concat(", " + subjects.get(i).name());
                }
            }
        }
        if (!subjectsAsString.isEmpty()) {
            System.out.println("---SUBJECTS: " + subjectsAsString);
        }

        System.out.println("##########################################################");
    }

}
