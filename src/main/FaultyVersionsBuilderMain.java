package main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import data_import.pit.merged.PitAnalysisPreparation;
import data_import.pit.merged.StatisticsPrinter;

public class FaultyVersionsBuilderMain {

	/**
	 * for each PIT Projekt >> Import
	 * 		bilde 10 Faulty versions pro (PARAMETER: 1-15 Faults, Test Sizes >20 >100 >200 >500)
	 * speichern in faulty-versions.xml
	 * 
	 * Faulty Version also eigene Klasse aufnehmen: Muss alle Daten enthalten, welche zur Analyse benötigt werden
	 * 		TestCase[] tests 	Set faults
	 * 		projectMetrics
	 * 
	 * In ma-clustering:
	 * 		pro PIT Projekt: Import faulty versions
	 * 			analyze each faulty version
	 * 				run all Prioritizations
	 * 				write evaluationEntries
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException {
		System.out.println("Program started...");
		FaultyVersionsBuilder builder = new FaultyVersionsBuilder();
		String projectName = "commons-geometry";
		String dir = "C:\\study\\SWDiag\\sharedFolder_UbuntuVM\\MA\\pit_data\\" +  projectName + "\\pit-data\\";
		System.out.println("Building Faulty versions for project " + projectName);
		builder.processMergedPitProject(dir, projectName);
		
		//TODO: Serialized xml datei wird sehr groß, prüfen ob die für z.b. 100 versionen aus einem PIT projekt nicht zu rießig wird
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
