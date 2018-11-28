package main;

import java.io.IOException;

import data_import.pit.merged.PitAnalysisPreparation;

public class Main {

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
	public static void main(String[] args) throws IOException {
		System.out.println("Program started...");
		String projectName = "biojava";
		String dir = "C:\\study\\SWDiag\\sharedFolder_UbuntuVM\\MA\\pit_data_archiv\\" +  projectName + "\\pit-data\\";
		System.out.println("Building Faulty versions for project " + projectName);
		PitAnalysisPreparation prep = new PitAnalysisPreparation(dir);
		//TODO: AA_NEXT, faultyVersions objekte erstellen, dabei direkt projectMetrics füllen
		//			auf globals achten
	}
}
