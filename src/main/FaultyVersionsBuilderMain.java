package main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import data_import.pit.merged.PitAnalysisPreparation;
import data_import.pit.merged.StatisticsPrinter;
import fault_selection.FaultSelectionStrategy1;
import fault_selection.PitFaultSelectionStrategyBase;

public class FaultyVersionsBuilderMain {
	public static final String PIT_MUTATIONS_BASE_DIR = "C:\\study\\SWDiag\\sharedFolder_UbuntuVM\\MA\\pit_data\\";
	public static final String PIT_DATA_FOLDER_NAME = "\\pit-data\\";
	
	private static final int MIN_FAULTS_COUNT = 1;
	private static final int MAX_FAULTS_COUNT = 20;
	private static final int VERSIONS_PER_FAULT_COUNT = 10;
	
	public static void main(String[] args) throws ParserConfigurationException {
		try {
		System.out.println("Program started...");
		File[] projectDirectories = new File(PIT_MUTATIONS_BASE_DIR).listFiles(File::isDirectory);
		for (File projectDir : projectDirectories) {
			String projectName = projectDir.getPath().substring(projectDir.getPath().lastIndexOf('\\') + 1, projectDir.getPath().length());
			System.out.println("Building Faulty versions for project " + projectName);
			FaultyVersionsBuilder builder = new FaultyVersionsBuilder(projectDir.getPath() + PIT_DATA_FOLDER_NAME, projectName);
			PitFaultSelectionStrategyBase selector = new FaultSelectionStrategy1(MIN_FAULTS_COUNT, MAX_FAULTS_COUNT, VERSIONS_PER_FAULT_COUNT, builder);
			selector.selectAndProcessFaultyVersions(builder.prep.getPitFaults(), builder.prep.getPitTests());
		}
		System.out.println("Building Faulty Versions program finished!");
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
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
