/*
 *
 * @author Scott Weaver
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IdealK {

	private static String TRAINING_FILE_PATH = "Data/TrainingData/training_data.csv";
    //Must include. This is the file you want to test against feature files
    private static String TEST_FILE_PATH = "Data/TestData/test_data.csv";
    //Knn will be tested from k=1 to k=MAX_K_VALUE
    private static int MAX_K_VALUE = 3;
    
	public static void main(String[] args) {
		
		System.out.println("Training File Path: " + TRAINING_FILE_PATH);
		System.out.println("Test File Path: " + TEST_FILE_PATH);
		
		ArrayList<double[]> trainingData = new ArrayList<>();
		ArrayList<double[]> testData = new ArrayList<>();
		ArrayList<String> trainingClassification = new ArrayList<>();
		ArrayList<String> testClassification = new ArrayList<>();
		
		if ((new File(TEST_FILE_PATH)).isFile() && (new File(TRAINING_FILE_PATH)).isFile()) {
			String trainingColumnHeaders = readFeatureFile(TRAINING_FILE_PATH, trainingData, trainingClassification);
			String testColumnHeaders = readFeatureFile(TEST_FILE_PATH, testData, testClassification);
			
			if (trainingColumnHeaders.equals(testColumnHeaders)) {
				findIdealK(trainingData, trainingClassification, testData, testClassification);
			} else {
				System.err.println("Features inconsistent between Test File and Training File (mismatching headers).");
			}
		} else {
			System.err.println("Test File or Training File does not exist.");
		}
	}
	
	private static void findIdealK(ArrayList<double[]> trainingData, ArrayList<String> trainingClassification, ArrayList<double[]> testData, ArrayList<String> testClassification) {
		HashMap<Integer,Integer> accuracy = new HashMap<>();
		int numTestData = testData.size();
		int maxK = MAX_K_VALUE;
		
		System.out.println("-----------------------------------");
		//For each test data point, perform KNN using k=1 to k=maxK
		for (int i = 0; i < numTestData; i++) {
			double[] test = testData.get(i);
			String actualClassification = testClassification.get(i);
			
			ArrayList<DistObj> distanceObjects = KnnUtilities.performKNN(trainingData, test);
			for (int k = 1; k <= maxK; k++) {
				String predictedClassification = getPredictedClassification(distanceObjects, trainingClassification, k);
				
				if (actualClassification.equals(predictedClassification)) {
					Integer count = accuracy.get(k);
					accuracy.put(k, count==null?1:count+1);
				}
			}	
		}
		
		int idealK = 0; // the lowest k with the highest classification
		int idealKFrequency = 0;
		
		System.out.println("-----------------------------------");
		//For each k value, print out the percentage of correctly classified tests using that k for KNN.
		
		for (int k : accuracy.keySet()) {
			int frequency = accuracy.get(k);
			
			if (frequency > idealKFrequency) {
    			idealKFrequency = frequency;
    			idealK = k;
    		}
			
			double acc = ((double) frequency) / numTestData;
			System.out.println("Accuracy for k = " + k + ": " + frequency + "/" + numTestData + " = " + acc);
		}
		
		System.out.println("-----------------------------------");
		// idealK is the lowest k with the highest classification
		System.out.println("Ideal k = " + idealK);
	}
	
	//Return the results of KNN (predicted classification) using k. Print out the percentage of training data that the test data matched up with
	private static String getPredictedClassification(ArrayList<DistObj> distanceObjects, ArrayList<String> trainingClassification, int k) {
		
		HashMap<String,Integer> numOccurances = new HashMap<>();
		for (int i = 0; i < k; i++) {
			int index = distanceObjects.get(i).index;
			String classification = trainingClassification.get(index);
			
			Integer count = numOccurances.get(classification);
			numOccurances.put(classification, count==null?1:count+1);
		}
		
		String classification = "";
		int max = 0;
		
		for (String key : numOccurances.keySet()) {
			int val = numOccurances.get(key);
			
			if (val > max) {
    			max = val;
    			classification = key;
    		}
		}
		
		System.out.println("Classification for " + classification + ", k = " + k + ": " + max + "/" + k);
		
		return classification;
		
	}
	
	private static String readFeatureFile(String featureFilePath, ArrayList<double[]> featureData, ArrayList<String> featureClassification) {
		String columnHeaders = "";
		
		try {

	        BufferedReader bufferedReader = new BufferedReader(new FileReader(featureFilePath));
	        
	        columnHeaders = bufferedReader.readLine();

	        String line = bufferedReader.readLine();
	        int dataSize = line.length() - line.replace(",", "").length();
	
	        while (line != null) {
	        	String classification = line.substring(0, line.indexOf(","));
	            String dataCompsStr[] = line.substring(line.indexOf(",") + 1).split(",");
	
	            double dataComps[] = new double[dataSize];
	
	            for (int i = 0; i < dataSize; i++) {
	                dataComps[i] = Double.parseDouble(dataCompsStr[i]);
	            }
	
	            featureData.add(dataComps);
	            featureClassification.add(classification);
	            line = bufferedReader.readLine();
	        }
	        bufferedReader.close();
	        
        } catch(IOException e) {
        	System.err.println("Cannot read feature file.");
        }
		
		return columnHeaders;
	}	
}
