package com.gopyyn.salad.nlp;

import com.gopyyn.salad.core.SaladContext;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tutorialkart
 */
public class SentenceDetectorTraining {

	public static void main(String[] args) {
		try {
			new SentenceDetectorTraining().trainSentDetectModel();
		} catch (IOException|URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method generates s custom model file for sentence detection, in directory "custom_models".
	 * The training data used is "trainingDataSentences.txt". Training data contains a sentence per line in the text file.
	 * @throws IOException
	 */
	public void trainSentDetectModel() throws IOException, URISyntaxException {
		// directory to save the model file that is to be generated, create this directory in prior
		File destDir = new File(SaladContext.class.getClassLoader().getResource("models").toURI());

		// training data
//		preprocessor();
		InputStreamFactory in = new MarkableFileInputStreamFactory(SaladContext.retrieveFile("trainingDataSentences.txt"));

		// parameters used by machine learning algorithm, Maxent, to train its weights
		TrainingParameters mlParams = ModelUtil.createDefaultTrainingParameters();;
		mlParams.put("Iterations", 100);
		mlParams.put("Cutoff", 0);

		// train the model
		PlainTextByLineStream plainTextByLineStream = new PlainTextByLineStream(in, StandardCharsets.UTF_8);
		SentenceModel sentdetectModel = SentenceDetectorME.train(
				"en",
				new SentenceSampleStream(plainTextByLineStream),
				true,
				null,
				mlParams);

		// save the model, to a file, "en-sent-custom.bin", in the destDir : "custom_models"
		File outFile = new File(destDir,"en-sent-custom.bin");
		System.out.println("model path: " + outFile.getAbsolutePath());
		FileOutputStream outFileStream = new FileOutputStream(outFile); 
		sentdetectModel.serialize(outFileStream);

		// loading the model
		SentenceDetectorME sentDetector = new SentenceDetectorME(sentdetectModel); 

		// detecting sentences in the test string
		String testString = ("Then verify that \"Your authentication information is incorrect. Please try again.\" is \"visible\"");
		System.out.println("\nTest String: "+testString);
		String[] sents = sentDetector.sentDetect(testString);
		System.out.println("---------Sentences Detected by the SentenceDetector ME class using the generated model-------");
		for(int i=0;i<sents.length;i++){
			System.out.println("Sentence "+(i+1)+" : "+sents[i]);
		}
	}

	public void preprocessor() throws IOException, URISyntaxException {

		// Read the input data file
		BufferedReader reader = new BufferedReader(new FileReader(SaladContext.retrieveFile("trainingDataSentences.txt")));

		// Create the output data file
		File destDir = new File(SaladContext.class.getClassLoader().getResource("resources/training").toURI());
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destDir,"trainingDataSentences_parsed.txt")));

		// Define a regular expression to match double-quoted strings
		Pattern pattern = Pattern.compile("\"(.*?)\"");

		String line;
		while ((line = reader.readLine()) != null) {
			// Find all double-quoted strings in the line
			Matcher matcher = pattern.matcher(line);

			// Replace all dots inside double-quoted strings with a special token
			String replacedLine = line;
			while (matcher.find()) {
				String quote = matcher.group();
				String replacedQuote = quote.replaceAll("\\.", "DOT");
				replacedLine = replacedLine.replace(quote, replacedQuote);
			}

			// Write the preprocessed line to the output file
			writer.write(replacedLine + "\n");
		}

		reader.close();
		writer.close();
	}

}