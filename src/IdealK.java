import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IdealK {

	private static String TRAINING_FILE_PATH = "Data/FeatureFiles/features_0_nomag.csv";
    //Must include. This is the file you want to test against feature files
    private static String TEST_FILE_PATH = "Data/TestData/testData.csv";
    
	public static void main(String[] args) {
		
		System.out.println("Training File Path: " + TRAINING_FILE_PATH);
		System.out.println("Test File Path: " + TEST_FILE_PATH);
		
		ArrayList<double[]> trainingData = new ArrayList<>();
		ArrayList<double[]> testData = new ArrayList<>();
		ArrayList<String> trainingClassification = new ArrayList<>();
		ArrayList<String> testClassification = new ArrayList<>();
		ArrayList<String> columnHeaders;
		
		if ((new File(TEST_FILE_PATH)).isFile() && (new File(TRAINING_FILE_PATH)).isFile()) {
			String trainingColumnHeaders = readFeatureFile(TRAINING_FILE_PATH, trainingData, trainingClassification);
			String testColumnHeaders = readFeatureFile(TEST_FILE_PATH, testData, testClassification);
			
			if (trainingColumnHeaders.equals(testColumnHeaders)) {
				columnHeaders = getColumnHeaders(trainingColumnHeaders);
			} else {
				System.err.println("Features inconsistent between Test File and Training File (mismatching headers).");
				findIdealK(trainingData, trainingClassification, testData, testClassification);
			}
		} else {
			System.err.println("Test File or Training File does not exist.");
		}
	}
	
	public static void findIdealK(ArrayList<double[]> trainingData, ArrayList<String> trainingClassification, ArrayList<double[]> testData, ArrayList<String> testClassification) {
		ArrayList<Double> accuracy = new ArrayList<>();
		int numFeatures = trainingData.get(0).length;
		
		
		for (double[] test : testData) {
			ArrayList<DistObj> distanceObjects = KnnUtilities.performKNN(trainingData, test);
			for (int k = 1; k <= numFeatures; k++) {
				int[] indices = new int[k];
				for (int i = 0; i < k; i++) {
					indices[i] = distanceObjects.get(i).index;
				}
				
				
			}
			
			
			
		}
		
	}
		/*
		arraylist accuracy of doubles
		
		for each testdata in testFile
		distobjects = performknn
			for k = 1 to numfeatures
				indices = indices of 1st k objects in distobjects
				test classification = majority classification of features with indices "indices"
				if training classification = test classification 
					accuracy[k]++
					
		for i = 1 to numfeatures
			if accuracy[i] is max
				maxindex = i
				maxaccuracy = accuracy[i]
						
		maxaccuracy /= numfeatures;
		
		
		
		print maxaccurcy and maxindex
	}*/
	
	public static String readFeatureFile(String featureFilePath, ArrayList<double[]> featureData, ArrayList<String> featureClassification) {
		String columnHeaders = "";
		
		try {

	        BufferedReader bufferedReader = new BufferedReader(new FileReader(featureFilePath));
	        
	        columnHeaders = bufferedReader.readLine();

	        String line = bufferedReader.readLine();
	        int dataSize = line.length() - line.replace(",", "").length();
	
	        double dataAvg[] = new double[dataSize];
	
	        while (line != null) {
	        	String classification = line.substring(0, line.indexOf(","));
	            String dataCompsStr[] = line.substring(line.indexOf(",") + 1).split(",");
	
	            double dataComps[] = new double[dataSize];
	
	            for (int i = 0; i < dataSize; i++) {
	                dataComps[i] = Double.parseDouble(dataCompsStr[i]);
	                dataAvg[i] += dataComps[i];
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
	
    private static ArrayList<String> getColumnHeaders(String line) {
    	ArrayList<String> columnHeaders = new ArrayList<>();
    	
    	String dataCompsStr[] = line.substring(line.indexOf(",") + 1).split(",");
    	
    	for (String feature : dataCompsStr) {
    		columnHeaders.add(feature);
    	}
    	
    	return columnHeaders;
    }
	
}
