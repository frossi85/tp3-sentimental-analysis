package com.example;

import com.example.sentiment.AnalyzeBagOfWords;
import com.example.sentiment.SentiWordNetDic;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Classifier {

	private static SentiWordNetDic dic = null;

	public static void main(String[] args) {

		String text= "";

		List<Integer> positiveResults = getResult("pos/")
				.stream()
				.map(value -> value > 0 ? 1 : 0)
				.collect(Collectors.toList());

		List<Integer> negativeResults = getResult("neg/")
				.stream()
				.map(value -> value > 0 ? 0 : 1)
				.collect(Collectors.toList());


		double positiveResultsCount = positiveResults.size();
		double positiveSuccessCount = positiveResults.stream().filter(x -> x == 1).count();
		double positiveSuccessPercentage = (positiveSuccessCount / positiveResultsCount) * 100;

		double negativeResultsCount = negativeResults.size();
		double negativeSuccessCount = negativeResults.stream().filter(x -> x == 1).count();
		double negativeSuccessPercentage = (negativeSuccessCount / negativeResultsCount) * 100;

		double totalPercentage = (positiveSuccessPercentage + negativeSuccessPercentage) / 2;

		System.out.println("El porcentaje de acierto de valores positivos es: " + positiveSuccessPercentage);
		System.out.println("El porcentaje de acierto de valores negativos es: " + negativeSuccessPercentage);
		System.out.println("El porcentaje de acierto de valores positivos es: " + totalPercentage);
	}

	private static List<Double> getResult(String folder) {
		return getFilesFromResourceFolder(folder)
				.stream()
				.map(file -> getPolarity(loadfile(file))).collect(Collectors.toList());
	}

	private static List<String> getFilesFromResourceFolder(String folder) {
		List<String> files = null;

		try {
			ClassLoader classLoader = Classifier.class.getClassLoader();
			File file = new File(classLoader.getResource(folder).getFile());
			files = IOUtils.readLines(
					classLoader.getResourceAsStream(folder),
					StandardCharsets.UTF_8
			).stream().map(fileName -> file.getAbsoluteFile() + "/" + fileName).collect(Collectors.toList());
		} catch (Exception e) {
			System.out.println(e);
		}

		return files;
	}
	
	private static double getPolarity(String text){
		AnalyzeBagOfWords abw =null;
		try {
			if(dic == null) {
				dic = new SentiWordNetDic();
			}
			abw = new AnalyzeBagOfWords(dic);
		} catch (IOException e) {
			System.out.println("Se produjo el siguiente error: "+e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		return abw.doAnalysis(text);
	}

	private static String loadfile(String filenamepath){
		StringBuilder sb=null;
		try (BufferedReader br = new BufferedReader(new FileReader(filenamepath))) {
			sb=new StringBuilder("");
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
