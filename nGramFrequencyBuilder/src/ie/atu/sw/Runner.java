package ie.atu.sw;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Runner {

	public static void main(String[] args) throws Exception {

		Menu menu = new Menu();
		menu.printIntro();
		ConsoleMenuItem[] menuItems = {
				new ConsoleMenuItem(ConsoleMenuOption.INPUT_DIRECTORY.getValue(), "Specify Text File Directory"),
				new ConsoleMenuItem(ConsoleMenuOption.N_GRAM_SIZE.getValue(), "Specify n-Gram Size"),
				new ConsoleMenuItem(ConsoleMenuOption.OUTPUT_FILE.getValue(), "Specify Output File"),
				new ConsoleMenuItem(ConsoleMenuOption.BUILD_N_GRAMS.getValue(), "Build n-Grams"),
				new ConsoleMenuItem(ConsoleMenuOption.QUIT.getValue(), "Quit")
		};
		ConsoleMenuOption selectedOption = ConsoleMenuOption.INVALID_ENTRY;
		while (selectedOption != ConsoleMenuOption.QUIT) {
			selectedOption = menu.printMainMenu(menuItems);
			switch (selectedOption) {
				case INPUT_DIRECTORY: {
					// user specified input directory
					System.out.print("Enter input directory:");
					Scanner s = new Scanner(System.in);
					String str = s.nextLine();
					menuItems[ConsoleMenuOption.INPUT_DIRECTORY.getValue() - 1].Value = str;
					break;
				}
				case N_GRAM_SIZE: {
					// user specified n-gram size
					System.out.print("Enter n-gram size:");
					Scanner s = new Scanner(System.in);
					String str = s.nextLine();
					try {
						int value = Integer.parseInt(str);
						if(!(value >= 1 && value <= 5)){
							showError("Invalid input for n-gram size, number should be between 1 and 5");
						} else {
							menuItems[ConsoleMenuOption.N_GRAM_SIZE.getValue() - 1].Value = str;
						}
					} catch (Exception e) {
						showError("Invalid input, expected numeric value");
					}
					break;
				}
				case OUTPUT_FILE: {
					// user specified output filename
					System.out.print("Enter output filename:");
					Scanner s = new Scanner(System.in);
					String str = s.nextLine();
					menuItems[ConsoleMenuOption.OUTPUT_FILE.getValue() - 1].Value = str;
					break;
				}
				case BUILD_N_GRAMS: {
					// user wants to build n-gram to a filename
					List<String> validationErrors = new ArrayList<String>();
					// validate all menu values
					ConsoleMenuItem inputDir = menuItems[ConsoleMenuOption.INPUT_DIRECTORY.getValue() - 1];
					List<File> inputFiles = null;
					if(isNullOrEmpty(inputDir.Value)){
						validationErrors.add("Please specify input directory before building n-gram");
					} else {
						if(!Files.exists(Path.of(inputDir.Value))){
							validationErrors.add("Input directory does not exist");
						} else {
							// filter files only, not folders
							inputFiles = Files.walk(Paths.get(inputDir.Value))
                                .filter(Files::isRegularFile)
                                .map(Path::toFile)
                                .collect(Collectors.toList()); 
							if(inputFiles.size() == 0){
								validationErrors.add("Input directory does not contain any files");
							};
						}
					}
					if(isNullOrEmpty(menuItems[ConsoleMenuOption.OUTPUT_FILE.getValue() - 1].Value)){
						validationErrors.add("Please specify output filename before building n-gram");
					}
					if(isNullOrEmpty(menuItems[ConsoleMenuOption.N_GRAM_SIZE.getValue() - 1].Value)){
						validationErrors.add("Please specify n-gram size before building n-gram");
					}

					// show errors if any
					if(validationErrors.size()>0){
						for (String error : validationErrors) {
							showError(error);
						}
						break;
					}

					
					System.out.println("Reading " + inputFiles.size() + " input files...");
					String[] inputFileText = new String[(int) inputFiles.size()];
					for(int i=0;i<inputFiles.size();i++){
						
						inputFileText[i] = readTextFile(inputFiles.get(i).getAbsolutePath()).trim().replaceAll("[^a-zA-Z]", "").toLowerCase();
					}
					System.out.println("1/2 Generating n-grams...");
					System.out.print(ConsoleColour.YELLOW); // Change the colour of the console text for progress
					int numberOfNGrams = Integer.parseInt(menuItems[ConsoleMenuOption.N_GRAM_SIZE.getValue() - 1].Value);
					Map<String, Integer> nGramCount = new HashMap<String, Integer>();
					for(int i=0;i<inputFileText.length;i++){
						List<String> nGrams = buildNGrams(numberOfNGrams, inputFileText[i]);
						for (String nGram : nGrams) {
							if(nGramCount.containsKey(nGram)){
								int count = nGramCount.get(nGram);
								count++;
								nGramCount.replace(nGram, count);
							} else {
								nGramCount.put(nGram, 1);
							}
						}
						Progress.printProgress(i + 1, inputFileText.length); // After each (some) steps, update the progress meter
					}
					System.out.println(ConsoleColour.RESET); // Change the colour of the console text
					System.out.println("2/2 Storing n-grams to CSV...");
					String outputFileName = menuItems[ConsoleMenuOption.OUTPUT_FILE.getValue() - 1].Value;
					File outputFile = new File(outputFileName);
					// if we already have the same file, delete it
					if(outputFile.exists()){
						outputFile.delete();
					}
					BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
					int iWriteCounter = 1;
					System.out.print(ConsoleColour.YELLOW); // Change the colour of the console text for progress
					for (String key : nGramCount.keySet()) {
						writer.write(key + "," + nGramCount.get(key));
						writer.newLine();
						Progress.printProgress(iWriteCounter, nGramCount.size()); // After each (some) steps, update the progress meter
						iWriteCounter++;
					}
					writer.close();
					System.out.print(ConsoleColour.RESET); // Change the colour of the console text for progress
					break;
				}
				case QUIT: {
					// user wants to quit
					System.out.println("Bye bye!");
					break;
				}
				case INVALID_ENTRY: {
					// invalid entry from the user
					showError("Invalid entry, please try again from the following options:");
					break;
				}
			}
		}

	}

	// determine if string is null or empty
	private static boolean isNullOrEmpty(String value) {
		return (value == null || value.isEmpty() || value.trim().isEmpty());
	}

	//utility function to read text from a file
	private static String readTextFile(String absolutePath) {
		try(BufferedReader br = new BufferedReader(new FileReader(absolutePath))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String content = sb.toString();
			return content;
		}
		catch(Exception ex){
			showError("file cannot be read [" + absolutePath + "]");
			return "";
		}
	}

	// show console error
	private static void showError(String error) {
		System.out.print(ConsoleColour.RED); // Change the colour of the console text
		System.out.println(error);
		System.out.print(ConsoleColour.RESET); // Change the colour of the console text
	}

	// function to build n grams from a string
	public static List<String> buildNGrams(int numberOfNGrams, String input) {
		List<String> ngrams = new ArrayList<String>();
		for (int i = 0; i < input.length() - numberOfNGrams + 1; i++)
		{
			// Add the substring or size n
			ngrams.add(input.substring(i, i + numberOfNGrams));
		}
		return ngrams;
	  }
}