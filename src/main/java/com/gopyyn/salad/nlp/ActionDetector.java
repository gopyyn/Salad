package com.gopyyn.salad.nlp;

import com.gopyyn.salad.core.SaladContext;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ActionDetector {

    public static void main(String[] args) throws IOException {

        // Train categorizer model to the training data we created.
        DoccatModel model = trainCategorizerModel();

        // Take chat inputs from console (user) in a loop.
        Scanner scanner = new Scanner(System.in);
        while (true) {

            // Get chat input from user.
            System.out.println("##### You:");
            String userInput = scanner.nextLine();

            // Break users chat input into sentences using sentence detection.
            String[] sentences = breakSentences(userInput);

            String answer = "";
            boolean conversationComplete = false;

            // Loop through sentences.
            for (String sentence : sentences) {

                // Separate words from each sentence using tokenizer.
                String[] tokens = tokenizeSentence(sentence);

                // Tag separated words with POS tags to understand their gramatical structure.
                String[] posTags = detectPOSTags(tokens);

                // Lemmatize each word so that its easy to categorize.
                String[] lemmas = lemmatizeTokens(tokens, posTags);

                // Determine BEST action using lemmatized tokens used a mode that we trained
                // at start.
                String category = detectAction(model, lemmas);

                // Get salad command
                answer = answer + getSaladCommand(category, sentence) + "\n";

                // If category conversation-complete, we will end chat conversation.
                if ("conversation-complete".equals(category)) {
                    conversationComplete = true;
                }
            }

            // Print answer back to user. If conversation is marked as complete, then end
            // loop & program.
            System.out.println("##### Chat Bot: " + answer);
            if (conversationComplete) {
                break;
            }

        }

    }

    private static String getSaladCommand(String category, String sentence) {
        String[] quotedString = getQuotedString(sentence);
        if (quotedString.length == 0) {
            return category;
        }

        String variables = Arrays.stream(quotedString).reduce("", (s, s2) -> s + " " + s2);
        return StringUtils.join(asList(category, variables), " ");
    }

    private static String[] getQuotedString(String sentence) {
        return StringUtils.substringsBetween(sentence, "\"", "\"");
    }

    private static DoccatModel trainCategorizerModel() throws IOException {
        // actions-categories is a custom training data with categories to detect our actions
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(SaladContext.retrieveFile("actions-categories.txt"));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 0);

        // Train a model with classifications from above file.
        DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);
        return model;
    }

    /**
     * Detect category using given token. Use categorizer feature of Apache OpenNLP.
     *
     * @param model
     * @param finalTokens
     * @return
     * @throws IOException
     */
    private static String detectAction(DoccatModel model, String[] finalTokens) {
        // Initialize document categorizer tool
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

        // Get best possible action.
        double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
        System.out.print("probabilitiesOfOutcomes: " +  myCategorizer.getAllResults(probabilitiesOfOutcomes));
        String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
        System.out.println("\nCategory: " + category);

        return category;
    }

    /**
     * Break data into sentences using sentence detection feature of Apache OpenNLP.
     *
     * @param data
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String[] breakSentences(String data) throws IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.
        try (InputStream modelIn = ActionDetector.class.getResourceAsStream("/models/en-sent.bin")) {

            SentenceDetectorME myCategorizer = new SentenceDetectorME(new SentenceModel(modelIn));

            String[] sentences = myCategorizer.sentDetect(data);
            System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));

            return sentences;
        }
    }

    public ActionDetector() {
    }

    /**
     * Break sentence into words & punctuation marks using tokenizer feature of
     * Apache OpenNLP.
     *
     * @param sentence
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String[] tokenizeSentence(String sentence) throws IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.
        try (InputStream modelIn = ActionDetector.class.getResourceAsStream("/models/en-token.bin")) {
            // Initialize tokenizer tool
            TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(modelIn));

            // Tokenize sentence.
            String[] tokens = myCategorizer.tokenize(sentence);
            System.out.println("Tokenizer : " + Arrays.stream(tokens).collect(Collectors.joining(" | ")));

            return tokens;
        }
    }

    /**
     * Find part-of-speech or POS tags of all tokens using POS tagger feature of
     * Apache OpenNLP.
     *
     * @param tokens
     * @return
     * @throws IOException
     */
    private static String[] detectPOSTags(String[] tokens) throws IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.
        try (InputStream modelIn = ActionDetector.class.getResourceAsStream("/models/en-pos-maxent.bin")) {
            // Initialize POS tagger tool
            POSTaggerME myCategorizer = new POSTaggerME(new POSModel(modelIn));

            // Tag sentence.
            String[] posTokens = myCategorizer.tag(tokens);
            System.out.println("POS Tags : " + Arrays.stream(posTokens).collect(Collectors.joining(" | ")));

            return posTokens;
        }
    }

    /**
     * Find lemma of tokens using lemmatizer feature of Apache OpenNLP.
     *
     * @param tokens
     * @param posTags
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    private static String[] lemmatizeTokens(String[] tokens, String[] posTags)
            throws IOException {
        try (InputStream dictLemmatizer = ActionDetector.class.getResourceAsStream("/models/en-lemmatizer.dict")) {

            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
            String[] lemmaTokens = lemmatizer.lemmatize(tokens, posTags);
            System.out.println("Lemmatizer : " + Arrays.stream(lemmaTokens).collect(Collectors.joining(" | ")));

            return lemmaTokens;
        }
    }


}
