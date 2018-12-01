package data_import.pit.merged;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private final int MIN_TEST_SIZE;
	
	public PitAnalysisPreparation(String projectDir, int minPitTestSize) throws IOException {
		MIN_TEST_SIZE = minPitTestSize;
		pitMethods = PitMergedMutationsReader.readPitMergedMethods(projectDir);
		initPitTestsAndFaults();
		deleteSmallPitTests();
	}
	/**
	 * Collects all tests that are smaller (covered Methods) than the passed threshold.
	 * Removes the tests from the List and all references in PitMethods and PitMutations.
	 * Also updates the killingTests and coveringTests strings.
	 */
	private void deleteSmallPitTests() {
		Set<PitTestCase> smallPitTests = new HashSet<PitTestCase>();
		Iterator<PitTestCase> iterTests = pitTests.iterator();
		while (iterTests.hasNext()) {
			PitTestCase pitTest = iterTests.next();
			if (isSmallPitTest(pitTest)) {
				smallPitTests.add(pitTest);
				iterTests.remove();
			}
		}
		deleteTestsFromMethods(smallPitTests);
		deleteTestsFromFaults(smallPitTests);
	}
	/**
	 * Removes the small tests that shouldn't be considered in the prioritization from the pitMethods.
	 * Also removes methods that are not covered by any test anymore.
	 */
	private void deleteTestsFromMethods(Set<PitTestCase> pitTestsRemoved) {
		Iterator<PitMethod> iterMethods = pitMethods.iterator();
		while (iterMethods.hasNext()) {
			PitMethod pitMethod = iterMethods.next();
			pitMethod.getCoveringTests().removeAll(pitTestsRemoved);
			updateCoveringTestsNames(pitMethod.getCoveringTestsNames(), pitMethod.getCoveringTests());
			if (pitMethod.getCoveringTests().isEmpty()) {
				iterMethods.remove();
			}
		}
	}
	/**
	 * Removes the small tests that shouldn't be considered in the prioritization from the pitFaults.
	 * Also removes faults that are not killed by any test anymore.
	 */
	private void deleteTestsFromFaults(Set<PitTestCase> pitTestsRemoved) {
		Iterator<PitMutation> iterFaults = pitFaults.iterator();
		while(iterFaults.hasNext()) {
			PitMutation pitFault = iterFaults.next();
			pitFault.getKillingTests().removeAll(pitTestsRemoved);
			updateCoveringTestsNames(pitFault.getKillingTestsNames(), pitFault.getKillingTests());
			if (pitFault.getKillingTests().isEmpty()) {
				iterFaults.remove();
			}
		}
	}
	private boolean isSmallPitTest(PitTestCase pitTest) {
		if (pitTest.getCoveredMethods().size() < MIN_TEST_SIZE) {
			return true;
		}
		return false;
	}
	/**
	 * Updates the passed Set that contains the names of the covering tests.
	 */
	private void updateCoveringTestsNames(Set<String> coveringTestsNames, Set<PitTestCase> coveringTestsNew) {
		coveringTestsNames.clear();
		for (PitTestCase pitTest: coveringTestsNew) {
			coveringTestsNames.add(pitTest.getName());
		}
	}
	
	/**	creates PitTestCase objects and the links to PitMethods and PitMutations.
	 *  Also fills the pit faults set.
	 */
	private void initPitTestsAndFaults() {
		Map<String, PitTestCase> pitTestsMap = new HashMap<String, PitTestCase>();
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
