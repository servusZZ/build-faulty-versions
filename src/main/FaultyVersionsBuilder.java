package main;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import data_import.pit.merged.PitAnalysisPreparation;
import data_import.pit.merged.PitDataObjectsConverter;
import directories.globals.Directories;
import fault_selection.FaultSelectionStrategy1;
import fault_selection.PitFaultSelectionStrategyBase;
import faulty_project.globals.FaultyProjectGlobals;
import pit.data_objects.PitMutation;
import prioritization.data_objects.FaultyVersion;
import prioritization.evaluation.ProjectEvaluationEntry;

public class FaultyVersionsBuilder {
	private PitAnalysisPreparation prep;
	private final int MIN_TEST_SIZE = 20;
	
	private List<Set<PitMutation>> importPitFaultyVersions(String dir) throws IOException {
		// import pit merged methods
		prep = new PitAnalysisPreparation(dir, MIN_TEST_SIZE);
		PitFaultSelectionStrategyBase faultSelectionStrategy = new FaultSelectionStrategy1(1, 20, 5);
		return faultSelectionStrategy.selectFaultyVersions(prep.getPitFaults(), prep.getPitTests());
	}

	
	public void processMergedPitProject(String dir, String projectName) throws IOException {
		List<Set<PitMutation>> pitFaultyVersions = importPitFaultyVersions(dir);
		
		// create FaultyVersion object for each pitFaultyVersion
		List<FaultyVersion> faultyVersions = new ArrayList<FaultyVersion>();
		int faultyProjectId = 1;
		for (Set<PitMutation> pitFaultyVersion: pitFaultyVersions) {
			PitDataObjectsConverter converter = prep.initTestsAndFaults(pitFaultyVersion);
			System.out.println("Building next faulty Version with " + converter.getFaults().size() + " faults, " + converter.getFailures().length + " failures, " + converter.getPassedTCs().length + " passing Test Cases and " + FaultyProjectGlobals.methodsCount + " relevant methods.");
			ProjectEvaluationEntry projectMetrics = new ProjectEvaluationEntry(faultyProjectId, projectName,
					converter.getFaults().size(), converter.getFailures().length,
					converter.getPassedTCs().length, FaultyProjectGlobals.methodsCount, MIN_TEST_SIZE);
			faultyProjectId++;
			faultyVersions.add(new FaultyVersion(converter.getFailures(), converter.getPassedTCs(),
					converter.getFaults(), projectMetrics));
		}
		writeFaultyVersionsFile(dir, faultyVersions);
	}
	
	private void writeFaultyVersionsFile(String outputDir, List<FaultyVersion> faultyVersions) throws IOException {
		String outputFile = outputDir + Directories.FAULTY_VERSIONS_FILE_NAME;
		Path outputFilePath = Paths.get(outputFile);
		Files.deleteIfExists(outputFilePath);
		
		System.out.println("INFO: Writing all faulty versions to file " + outputDir + Directories.FAULTY_VERSIONS_FILE_NAME);
		FileOutputStream fos = new FileOutputStream(new File(outputDir + Directories.FAULTY_VERSIONS_FILE_NAME));
		XMLEncoder encoder = new XMLEncoder(fos);
		encoder.writeObject(faultyVersions);
		encoder.close();
		fos.close();
	}
}
