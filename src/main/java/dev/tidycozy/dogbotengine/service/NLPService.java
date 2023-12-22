package dev.tidycozy.dogbotengine.service;

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

@Service
public class NLPService {

    SentenceDetectorME sentenceDetectorME;

    TokenizerME tokenizerME;

    POSTaggerME posTaggerME;

    DictionaryLemmatizer dictionaryLemmatizer;

    public NLPService() {
        initSentenceDetectorME();
        initTokenizerME();
        initPOSTaggerME();
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
//                    getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin");
                    getClass().getResourceAsStream("/models/en-pos-maxent.bin");
            POSModel posModel = new POSModel(inputStream);
            posTaggerME = new POSTaggerME(posModel);
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
        String[] sentences = detectSentences(phrase);
        for (String sentence : sentences) {
            System.out.println("Sentence: " + sentence);
            String[] tokens = tokenizeSentence(sentence);
            System.out.println("Tokens: " + String.join(", ", tokens));
            String[] tags = tagTokens(tokens);
            System.out.println("Tags: " + String.join(", ", tags));
            String[] lemmas = dictionaryLemmatizer.lemmatize(tokens, tags);
            System.out.println("Lemmas: " + String.join(", ", lemmas));
        }
        return "I'm dogbot!";
    }

    private String[] detectSentences(String phrase) {
        return sentenceDetectorME.sentDetect(phrase);
    }

    private String[] tokenizeSentence(String sentence) {
        return tokenizerME.tokenize(sentence);
    }

    private String[] tagTokens(String[] tokens) {
        return posTaggerME.tag(tokens);
    }

}
