package com.gopyyn.salad.nlp;

import com.gopyyn.salad.core.SaladContext;
import com.gopyyn.salad.enums.ActionMapEnum;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.doccat.NGramFeatureGenerator;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ActionDetector {

    private static final DocumentCategorizerME DOCUMENT_CATEGORIZER_ME = getDocumentCategorizerME();
    private static final TokenizerME TOKENIZER_ME = getTokenizerME();
    private static final SentenceDetectorME SENTENCE_DETECTOR_ME = getSentenceDetectorME();
    private static final POSTaggerME POS_TAGGER_ME = getPOSTaggerME();
    private static final DictionaryLemmatizer DICTIONARY_LEMMATIZER = getDictionaryLemmatizer();

    public static List<String> findSaladAction(String userInput) throws IOException, URISyntaxException {
        // Break users chat input into sentences using sentence detection.
        String[] sentences = breakSentences(userInput);

        List<String> saladSyntax = new ArrayList<>();
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
            String category = detectAction(lemmas);
            String[] quotedString = getQuotedString(sentence);

            if (quotedString == null || quotedString.length == 0) {
                System.out.println("no paramentes passed. for action: "+category);
            } else {
                // Get salad command
                ActionMapEnum actionMapEnum = ActionMapEnum.valueOf(category.toUpperCase());
                String parsedAction = actionMapEnum.getSaladCommand(quotedString);
                System.out.println(parsedAction);
                saladSyntax.add(parsedAction);
                actionMapEnum.executeCommand(quotedString);
            }
        }
        return saladSyntax;
    }

    private static String getSaladCommand(String category, String[] quotedString) {
        String variables = Arrays.stream(quotedString).reduce("", (s, s2) -> s + " \"" + s2 + "\"");
        return StringUtils.join(asList(category, variables), " ");
    }

    private static String[] getQuotedString(String sentence) {
        return StringUtils.substringsBetween(sentence, "\"", "\"");
    }

    private static DoccatModel getCategorizerModel() {
        try {

            // actions-categories is a custom training data with categories to detect our actions
            InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(retrieveFile("actions-categories.txt"));
            ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

            DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator(), new NGramFeatureGenerator(2, 4) });

            TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
            params.put(TrainingParameters.CUTOFF_PARAM, 0);

            // Train a model with classifications from above file.
            DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);
            return model;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File retrieveFile(String filePath) throws URISyntaxException {
        return SaladContext.retrieveFile(filePath);
    }

    /**
     * Detect category using given token. Use categorizer feature of Apache OpenNLP.
     *
     * @param finalTokens
     * @return
     * @throws IOException
     */
    private static String detectAction(String[] finalTokens) {
        // Get best possible action.
        double[] probabilitiesOfOutcomes = DOCUMENT_CATEGORIZER_ME.categorize(finalTokens);
        BigDecimal equalProbablilityToAll = new BigDecimal(1 / probabilitiesOfOutcomes.length).setScale(3, RoundingMode.HALF_UP);

        if (equalProbablilityToAll.equals(new BigDecimal(probabilitiesOfOutcomes[0]))) {
            System.out.print("No possible action found: " + StringUtils.join(finalTokens));
            return "";
        }
        System.out.print("probabilitiesOfOutcomes: " +  DOCUMENT_CATEGORIZER_ME.getAllResults(probabilitiesOfOutcomes));
        String category = DOCUMENT_CATEGORIZER_ME.getBestCategory(probabilitiesOfOutcomes);
        System.out.println("\nCategory: " + category);

        return category;
    }

    private static DocumentCategorizerME getDocumentCategorizerME() {
        try (InputStream modelIn = ActionDetector.class.getClassLoader().getResourceAsStream("models/en-cat-custom.bin")) {
            // Initialize tokenizer tool
            return new DocumentCategorizerME(new DoccatModel(modelIn));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Break data into sentences using sentence detection feature of Apache OpenNLP.
     *
     * @param data
     * @return
     */
    public static String[] breakSentences(String data) {
        String[] sentences = SENTENCE_DETECTOR_ME.sentDetect(preProcessor(data));
        sentences = Arrays.stream(sentences).map(ActionDetector::postProcessor)
                            .collect(Collectors.toList()).toArray(new String[]{});
        System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));

        return sentences;
    }

    private static SentenceDetectorME getSentenceDetectorME() {
        try (InputStream modelIn = ActionDetector.class.getClassLoader().getResourceAsStream("models/en-sent-custom.bin")) {
            return new SentenceDetectorME(new SentenceModel(modelIn));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ActionDetector() {
    }

    /**
     * Break sentence into words & punctuation marks using tokenizer feature of
     * Apache OpenNLP.
     *
     * @param sentence
     */
    private static String[] tokenizeSentence(String sentence) throws IOException {
        String[] tokens = TOKENIZER_ME.tokenize(sentence);
        System.out.println("Tokenizer : " + Arrays.stream(tokens).collect(Collectors.joining(" | ")));

        return tokens;
    }

    private static TokenizerME getTokenizerME() {
        try (InputStream modelIn = ActionDetector.class.getClassLoader().getResourceAsStream("models/en-token.bin")) {
            // Initialize tokenizer tool
            return new TokenizerME(new TokenizerModel(modelIn));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find part-of-speech or POS tags of all tokens using POS tagger feature of
     * Apache OpenNLP.
     *
     * @param tokens
     */
    private static String[] detectPOSTags(String[] tokens) throws IOException {
        String[] posTokens = POS_TAGGER_ME.tag(tokens);
        System.out.println("POS Tags : " + Arrays.stream(posTokens).collect(Collectors.joining(" | ")));

        return posTokens;
    }
    private static POSTaggerME getPOSTaggerME() {
        try (InputStream modelIn = ActionDetector.class.getClassLoader().getResourceAsStream("models/en-pos-maxent.bin")) {
            // Initialize POS tagger tool
            return new POSTaggerME(new POSModel(modelIn));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find lemma of tokens using lemmatizer feature of Apache OpenNLP.
     *
     * @param tokens
     * @param posTags
     * @return
     */
    private static String[] lemmatizeTokens(String[] tokens, String[] posTags) {
        String[] lemmaTokens = DICTIONARY_LEMMATIZER.lemmatize(tokens, posTags);
        System.out.println("Lemmatizer : " + Arrays.stream(lemmaTokens).collect(Collectors.joining(" | ")));

        return lemmaTokens;
    }

    private static DictionaryLemmatizer getDictionaryLemmatizer() {
        try (InputStream dictLemmatizer = ActionDetector.class.getClassLoader().getResourceAsStream("models/en-lemmatizer.dict")) {
            return new DictionaryLemmatizer(dictLemmatizer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String preProcessor(String line) {
        try {
            // Define a regular expression to match double-quoted strings
            Pattern pattern = Pattern.compile("\"(.*?)\"");

            Matcher matcher = pattern.matcher(line);

            // Replace all dots inside double-quoted strings with a special token
            String replacedLine = line;
            while (matcher.find()) {
                String quote = matcher.group();
                String replacedQuote = quote.replaceAll("\\.", "DOT");
                replacedLine = replacedLine.replace(quote, replacedQuote);
            }

            return replacedLine;
        } catch (Exception e) {
            System.out.println(e);
        }

        return line;
    }
    public static String postProcessor(String line) {
        try {
            // Define a regular expression to match double-quoted strings
            Pattern pattern = Pattern.compile("\"(.*?)\"");

            Matcher matcher = pattern.matcher(line);

            // Replace all dots inside double-quoted strings with a special token
            String replacedLine = line;
            while (matcher.find()) {
                String quote = matcher.group();
                String replacedQuote = quote.replaceAll("DOT", ".");
                replacedLine = replacedLine.replace(quote, replacedQuote);
            }

            return replacedLine;
        } catch (Exception e) {
            System.out.println(e);
        }

        return line;
    }

}
