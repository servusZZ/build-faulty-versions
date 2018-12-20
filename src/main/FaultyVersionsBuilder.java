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
import fault_selection.FaultSelectionStrategy1;
import fault_selection.PitFaultSelectionStrategyBase;
import faulty_project.evaluation.ProjectEvaluationUtils;
import faulty_project.globals.FaultyProjectGlobals;
import pit.data_objects.PitMutation;
import prioritization.data_objects.FaultyVersion;
import prioritization.evaluation.ProjectEvaluationEntry;
import prioritization.evaluation.TestSuiteEvaluationEntry;

public class FaultyVersionsBuilder {
	public PitAnalysisPreparation prep;
	private final int MIN_TEST_SIZE = 20;
	
	private int filesCounter = 1;
	private String dir;
	private String projectName;
	
	public FaultyVersionsBuilder(String dir, String projectName) throws IOException {
		this.dir = dir;
		this.projectName = projectName;
		prep = new PitAnalysisPreparation(dir, MIN_TEST_SIZE);
	}
	
//	private List<Set<PitMutation>> importPitFaultyVersions(String dir) throws IOException {
//		PitFaultSelectionStrategyBase faultSelectionStrategy = new FaultSelectionStrategy1(
//				MIN_FAULTS_COUNT, MAX_FAULTS_COUNT, VERSIONS_PER_FAULT_COUNT);
//		return faultSelectionStrategy.selectFaultyVersions(prep.getPitFaults(), prep.getPitTests());
//	}
	
//	public void processMergedPitProject(String dir, String projectName) throws IOException {
//		List<Set<PitMutation>> pitFaultyVersions = importPitFaultyVersions(dir);
//		
//		// create FaultyVersion object for each pitFaultyVersion
//		List<FaultyVersion> faultyVersions = new ArrayList<FaultyVersion>();
//		int faultyProjectId = 1;
//		for (Set<PitMutation> pitFaultyVersion: pitFaultyVersions) {
//			PitDataObjectsConverter converter = prep.initTestsAndFaults(pitFaultyVersion);
//			System.out.println("Building next faulty Version " + projectName + "-" + faultyProjectId + " with " + converter.getFaults().size() + " faults, " + converter.getFailures().length + " failures, " + converter.getPassedTCs().length + " passing Test Cases and " + FaultyProjectGlobals.methodsCount + " relevant methods.");
//			ProjectEvaluationEntry projectMetrics = createProjectEvaluationEntry(faultyProjectId, projectName, converter);
//			faultyProjectId++;
//			faultyVersions.add(new FaultyVersion(converter.getFailures(), converter.getPassedTCs(),
//					converter.getFaults(), projectMetrics));
//		}
//		writeFaultyVersionsFile(dir, faultyVersions);
//	}
	
	/**
	 * Creates faulty versions for the passed pit faulty versions and writes them.
	 * Returns the last faultyProjectId + 1 (it was not yet written).
	 */
	public int createAndWriteFaultyVersions(List<Set<PitMutation>> pitFaultyVersions, int nextFaultyProjectId) throws IOException {
		List<FaultyVersion> faultyVersions = new ArrayList<FaultyVersion>();
		int faultyProjectId = nextFaultyProjectId;
		for (Set<PitMutation> pitFaultyVersion: pitFaultyVersions) {
			PitDataObjectsConverter converter = prep.initTestsAndFaults(pitFaultyVersion);
			System.out.println("Building next faulty Version " + projectName + "-" + faultyProjectId + " with " + converter.getFaults().size() + " faults, " + converter.getFailures().length + " failures, " + converter.getPassedTCs().length + " passing Test Cases and " + FaultyProjectGlobals.methodsCount + " relevant methods.");
			ProjectEvaluationEntry projectMetrics = createProjectEvaluationEntry(faultyProjectId, projectName, converter);
			faultyProjectId++;
			faultyVersions.add(new FaultyVersion(converter.getFailures(), converter.getPassedTCs(),
					converter.getFaults(), projectMetrics));
		}
		writeFaultyVersionsFile(dir, faultyVersions);
		return faultyProjectId;
	}
	
	private ProjectEvaluationEntry createProjectEvaluationEntry(int faultyProjectId, String projectName, PitDataObjectsConverter converter) {
		ProjectEvaluationEntry projectMetrics =  new ProjectEvaluationEntry(faultyProjectId, projectName,
				converter.getFaults().size(), converter.getFailures().length,
				ProjectEvaluationUtils.getFailuresWithMultipleFaultsCount(converter.getFailures()),
				converter.getPassedTCs().length, FaultyProjectGlobals.methodsCount,
				ProjectEvaluationUtils.getFaultsInSameClassPairsCount(converter.getFaults()),
				ProjectEvaluationUtils.getFaultsInSamePackagePairsCount(converter.getFaults()));
		
		double[] dduMetrics = ProjectEvaluationUtils.calculateDDU(converter.getFailures(), converter.getPassedTCs());
		
		TestSuiteEvaluationEntry testSuiteMetrics = new TestSuiteEvaluationEntry(FaultyProjectGlobals.testsCount,
				MIN_TEST_SIZE, ProjectEvaluationUtils.getMedianTestSize(converter.getFailures()),
				ProjectEvaluationUtils.getAverageTestSize(converter.getFailures()),
				dduMetrics[3], dduMetrics[0], dduMetrics[1], dduMetrics[2]);
		projectMetrics.setTestSuiteMetrics(testSuiteMetrics);
		
		return projectMetrics;
	}
	
//	private void writeFaultyVersionsFiles(String outputDir, List<FaultyVersion> faultyVersions) throws IOException {
//		int filesCounter = 0;
//		for (int i = 0; i < faultyVersions.size(); i = i + 10) {
//			filesCounter++;
//			int toIndex = i + 10;
//			if (toIndex > faultyVersions.size()) {
//				toIndex = faultyVersions.size();
//			}
//			writeFaultyVersionsFile(outputDir, new ArrayList<FaultyVersion>(faultyVersions.subList(i, toIndex)));
//		}
//	}
	private void writeFaultyVersionsFile(String outputDir, List<FaultyVersion> faultyVersions) throws IOException {
		String outputFile = outputDir + "faulty-versions-" + filesCounter + ".xml";
		filesCounter++;
		Path outputFilePath = Paths.get(outputFile);
		Files.deleteIfExists(outputFilePath);
		
		System.out.println("INFO: Writing all faulty versions to file " + outputFile);
		FileOutputStream fos = new FileOutputStream(new File(outputFile));
		XMLEncoder encoder = new XMLEncoder(fos);
		encoder.writeObject(faultyVersions);
		encoder.close();
		fos.close();
	}
}
