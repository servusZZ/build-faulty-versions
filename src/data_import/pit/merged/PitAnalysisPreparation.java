package data_import.pit.merged;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import faulty_project.globals.FaultyProjectGlobals;
import pit.data_objects.EPitMutationStatus;
import pit.data_objects.PitMethod;
import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;
/**
 * Import all PitMethods and Tests. Inits the data objects needed for prioritization form 
 * pit data objects.
 */
public class PitAnalysisPreparation {
	private List<PitMethod> pitMethods;
	private List<PitMutation> pitFaults;
	private List<PitTestCase> pitTests;
	private Map<String, PitTestCase> pitTestsMap;
	
	public PitAnalysisPreparation(String projectDir) throws IOException {
		pitMethods = PitMergedMutationsReader.readPitMergedMethods(projectDir);
		initPitTestsAndFaults();
	}
	/**	creates PitTestCase objects and the links to PitMethods and PitMutations.
	 *  Also fills the pit faults set.
	 */
	private void initPitTestsAndFaults() {
		pitTestsMap = new HashMap<String, PitTestCase>();
		pitFaults = new ArrayList<PitMutation>();
		for (PitMethod method: pitMethods) {
			// create new test or update covering methods for existing test
			for (String testName: method.getCoveringTestsNames()) {
				PitTestCase test = pitTestsMap.get(testName);
				if (test == null) {
					test = new PitTestCase(testName);
					pitTestsMap.put(testName, test);
				}
				test.updateCoveredMethods(method);
				method.addCoveringTest(test);
			}
			// update possible Faults for each killing test of a mutation
			for (PitMutation mutation: method.getMutations()) {
				if (EPitMutationStatus.isFault(mutation)) {
					pitFaults.add(mutation);
					for (String killingTest: mutation.getKillingTestsNames()) {
						PitTestCase test = pitTestsMap.get(killingTest);
						test.addPossibleFault(mutation);
						mutation.addKillingTest(test);
					}
				}
			}
		}
		pitTests = new ArrayList<PitTestCase>(pitTestsMap.values());
	}
	/**
	 * Inits the data objects of types Fault and TestCase in order to start the prioritization.
	 * Also sets the global attributes methodsCount, failuresCount, passedTestsCount and testsCount.
	 * @param 	faultyVersion the faultyVersion for which the prioritization should be prepared.
	 * @return 	the data objects bundeled as a converter object,
	 * 			the data can be accessed via getter methods.
	 */
	public PitDataObjectsConverter initTestsAndFaults(Set<PitMutation> faultyVersion) {
		Set<PitTestCase> pitFailures = getPitFailuresFromFaultyVersion(faultyVersion);
		Set<PitMethod> relevantMethods = getRelevantMethods(pitFailures);
		Set<PitTestCase> pitPassedTCs = getRelevantPassingTests(relevantMethods, pitFailures);
		FaultyProjectGlobals.methodsCount = relevantMethods.size();
		FaultyProjectGlobals.failuresCount = pitFailures.size();
//		FaultyProjectGlobals.passedTestsCount = pitPassedTCs.size();
//		FaultyProjectGlobals.testsCount = FaultyProjectGlobals.failuresCount + FaultyProjectGlobals.passedTestsCount;
		return new PitDataObjectsConverter(faultyVersion, pitFailures, pitPassedTCs, relevantMethods);
	}
	private Set<PitTestCase> getPitFailuresFromFaultyVersion(Set<PitMutation> faults){
		Set<PitTestCase> failures = new HashSet<PitTestCase>();
		for (PitMutation fault: faults) {
			for (PitTestCase failure:fault.getKillingTests()) {
				failures.add(failure);
			}
		}
		return failures;
	}
	/**
	 * Returns all PitMethods which are covered by at least one failure.
	 */
	private Set<PitMethod> getRelevantMethods(Set<PitTestCase> failures){
		Set<PitMethod> relevantMethods = new HashSet<PitMethod>();
		for (PitTestCase failure:failures) {
			for (PitMethod coveredMethod: failure.getCoveredMethods()) {
				relevantMethods.add(coveredMethod);
			}
		}
		return relevantMethods;
	}
	/**
	 * Returns all passing PitTestCases which cover at least one relevant method.
	 */
	private Set<PitTestCase> getRelevantPassingTests(Set<PitMethod> relevantMethods, Set<PitTestCase> failures){
		Set<PitTestCase> relevantPassingTests = new HashSet<PitTestCase>();
		for (PitMethod relevantMethod: relevantMethods) {
			for (PitTestCase coveringTC: relevantMethod.getCoveringTests()) {
				if (!failures.contains(coveringTC)) {
					relevantPassingTests.add(coveringTC);
				}
			}
		}
		return relevantPassingTests;
	}
	public List<PitMethod> getPitMethods() {
		return pitMethods;
	}
	public List<PitTestCase> getPitTests() {
		return pitTests;
	}
	public List<PitMutation> getPitFaults() {
		return pitFaults;
	}
}
