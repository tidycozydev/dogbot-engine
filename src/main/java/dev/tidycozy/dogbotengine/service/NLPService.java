package dev.tidycozy.dogbotengine.service;

import dev.tidycozy.dogbotengine.model.Punctuation;
import dev.tidycozy.dogbotengine.model.Subject;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class NLPService {

    SentenceDetectorME sentenceDetectorME;

    TokenizerME tokenizerME;

    POSTaggerME posTaggerME;

    POSTaggerME posTaggerMEForLemmatizer;

    DictionaryLemmatizer dictionaryLemmatizer;

    public NLPService() {
        initSentenceDetectorME();
        initTokenizerME();
        initPOSTaggerME();
        initPOSTaggerMEForLemmatizer();
        initDictionaryLemmatizer();
    }

    private void initSentenceDetectorME() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin");
            SentenceModel model = new SentenceModel(inputStream);
            sentenceDetectorME = new SentenceDetectorME(model);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void initTokenizerME() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin");
            TokenizerModel model = new TokenizerModel(inputStream);
            tokenizerME = new TokenizerME(model);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void initPOSTaggerME() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin");
            POSModel posModel = new POSModel(inputStream);
            posTaggerME = new POSTaggerME(posModel);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void initPOSTaggerMEForLemmatizer() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/en-pos-maxent.bin");
            POSModel posModel = new POSModel(inputStream);
            posTaggerMEForLemmatizer = new POSTaggerME(posModel);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void initDictionaryLemmatizer() {
        try {
            InputStream inputStream =
                    getClass().getResourceAsStream("/models/en-lemmatizer.dict");
            dictionaryLemmatizer = new DictionaryLemmatizer(inputStream);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getDogbotAnswer(String phrase) {
        String[] sentences = sentenceDetectorME.sentDetect(phrase);

        for (String sentence : sentences) {
            String[] tokens = tokenizerME.tokenize(sentence);
            String[] tags = posTaggerME.tag(tokens);
            String[] tagsForLemmas = posTaggerMEForLemmatizer.tag(tokens);
            String[] lemmas = dictionaryLemmatizer.lemmatize(tokens, tagsForLemmas);

            Punctuation punctuation = findPunctuation(tokens, tags);

            List<Subject> subjects = findSubjects(tokens, tags, tagsForLemmas);

            debug(sentence, tokens, tags, tagsForLemmas, lemmas, punctuation, subjects);
        }

        return "I'm dogbot!";
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
            if (tagsForLemmas[i].equals("PRP")) {
                subjectList.add(Subject.getFromString(tokens[i]));
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

    private void debug(String sentence,
                       String[] tokens,
                       String[] tags,
                       String[] tagsForLemmas,
                       String[] lemmas,
                       Punctuation punctuation,
                       List<Subject> subjects) {
        System.out.println("---SENTENCE: " + sentence);
        System.out.println("-----TOKENS: " + String.join(", ", tokens));
        System.out.println("-------TAGS: " + String.join(", ", tags));
        System.out.println("TAGS4LEMMAS: " + String.join(", ", tagsForLemmas));
        System.out.println("-----LEMMAS: " + String.join(", ", lemmas));
        System.out.println("PUNCTUATION: " + punctuation);

        String subjectsAsString = "";
        if (!subjects.isEmpty()) {
            subjectsAsString = subjectsAsString.concat(subjects.get(0).getName());
            if (subjects.size() > 1) {
                for (int i = 1; i < subjects.size(); i++) {
                    subjectsAsString = subjectsAsString.concat(", " + subjects.get(i).getName());
                }
            }
        }
        if (!subjectsAsString.isEmpty()) {
            System.out.println("---SUBJECTS: " + subjectsAsString);
        }

        System.out.println("##########################################################");
    }

}
