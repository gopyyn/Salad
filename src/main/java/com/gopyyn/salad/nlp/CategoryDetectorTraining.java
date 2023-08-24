package com.gopyyn.salad.nlp;

import com.gopyyn.salad.core.SaladContext;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.doccat.NGramFeatureGenerator;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static com.gopyyn.salad.core.SaladContext.retrieveFile;

public class CategoryDetectorTraining {

    public static void main(String[] args) {
        new CategoryDetectorTraining().trainCategorizerModel();
    }

    private void trainCategorizerModel() {
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

            // save the model, to a file, "en-sent-custom.bin", in the destDir : "custom_models"
            File destDir = new File(SaladContext.class.getClassLoader().getResource("./models").toURI());
            File outFile = new File(destDir,"en-cat-custom.bin");
            System.out.println("model path: " + outFile.getAbsolutePath());
            FileOutputStream outFileStream = new FileOutputStream(outFile);
            model.serialize(outFileStream);

            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
                // Get best possible action.
                String lemmas = "enter|O|O|O|in|O|O|O|O|O|digit|O|or|O|alias|O";
                String[] finalToken = lemmas.split("\\|");
                double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalToken);
                System.out.print("probabilitiesOfOutcomes: " +  myCategorizer.getAllResults(probabilitiesOfOutcomes));
                String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
                System.out.println("\nCategory: " + category);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
