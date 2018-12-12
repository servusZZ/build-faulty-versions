package main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import data_import.pit.merged.PitAnalysisPreparation;
import data_import.pit.merged.StatisticsPrinter;

public class FaultyVersionsBuilderMain {
	public static final String PIT_MUTATIONS_BASE_DIR = "C:\\study\\SWDiag\\sharedFolder_UbuntuVM\\MA\\pit_data\\";
	public static final String PIT_DATA_FOLDER_NAME = "\\pit-data\\";
	public static void main(String[] args) throws IOException, ParserConfigurationException {
		System.out.println("Program started...");
		FaultyVersionsBuilder builder = new FaultyVersionsBuilder();
		File[] projectDirectories = new File(PIT_MUTATIONS_BASE_DIR).listFiles(File::isDirectory);
		for (File projectDir : projectDirectories) {
			String projectName = projectDir.getPath().substring(projectDir.getPath().lastIndexOf('\\') + 1, projectDir.getPath().length());
			System.out.println("Building Faulty versions for project " + projectName);
			builder.processMergedPitProject(projectDir.getPath() + PIT_DATA_FOLDER_NAME , projectName);
		}
		System.out.println("Building Faulty Versions program finished!");
	}
	
	@SuppressWarnings("unused")
	private static void printStatistics() throws ParserConfigurationException, IOException {
		String PIT_DATA_BASE_DIR = "C:\\study\\SWDiag\\sharedFolder_UbuntuVM\\MA\\pit_data";
		String PIT_DATA_FOLDER_NAME = "pit-data";
		
		File[] directories = new File(PIT_DATA_BASE_DIR).listFiles(File::isDirectory);
	    for (File projectDirectory: directories) {
	    	System.out.println("INFO: Printing statistics for project " + projectDirectory.getPath());
	    	PitAnalysisPreparation prep = new PitAnalysisPreparation(projectDirectory + "\\" + PIT_DATA_FOLDER_NAME + "\\", 1);
			StatisticsPrinter statisticsPrinter = new StatisticsPrinter(prep.getPitMethods(), prep.getPitTests());
			statisticsPrinter.printTestStatistics();
	    }
	}
}
