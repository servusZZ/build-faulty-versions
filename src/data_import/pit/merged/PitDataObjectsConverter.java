package data_import.pit.merged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pit.data_objects.PitMethod;
import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;
import prioritization.data_objects.Fault;
import prioritization.data_objects.TestCase;
import utils.DataImportUtils;

public class PitDataObjectsConverter {
	private TestCase[] failures;
	private TestCase[] passedTCs;
	private Set<Fault> faults;
	private Map<PitMethod, Integer> methodIndexMapping;
	
	public PitDataObjectsConverter(Set<PitMutation> pitFaults, Set<PitTestCase> pitFailures,
			Set<PitTestCase> pitPassedTCs, Set<PitMethod> relevantMethods) {
		initFaults(pitFaults);
		initMethodIndexMapping(relevantMethods);
		failures = initTestCases(pitFailures, false);
		passedTCs = initTestCases(pitPassedTCs, true);
		DataImportUtils.addFaultsToFailures(failures, faults);
	}
	private void initMethodIndexMapping(Set<PitMethod> relevantMethods) {
		methodIndexMapping = new HashMap<PitMethod, Integer>();
		int i = 0;
		for (PitMethod method: relevantMethods) {
			methodIndexMapping.put(method, i);
			i++;
		}
	}
	/**
	 * Creates TestCase objects for the passed pit test cases.
	 * The methodIndexMapping must already be set before calling this method.
	 * @param passed the value (true or false) to which the passed attribute of the created
	 * 		  TestCases will be set.
	 */
	private TestCase[] initTestCases(Set<PitTestCase> pitTestCases, boolean passed) {
		TestCase[] testCases = new TestCase[pitTestCases.size()];
		int i = 0;
		for (PitTestCase pitTestCase: pitTestCases) {
			testCases[i] = new TestCase(pitTestCase.getName(), passed, getMethodIndexes(pitTestCase.getCoveredMethods()));
			i++;
		}
		return testCases;
	}
	private Set<Integer> getMethodIndexes(Set<PitMethod> methods){
		Set<Integer> methodIndexes = new HashSet<Integer>();
		for (PitMethod method: methods) {
			methodIndexes.add(methodIndexMapping.get(method));
		}
		return methodIndexes;
	}
	private void initFaults(Set<PitMutation> pitFaults) {
		faults = new HashSet<Fault>();
		for (PitMutation pitFault: pitFaults) {
			Fault fault = new Fault(pitFault.getId(), new ArrayList<String>(pitFault.getKillingTestsNames()));
			faults.add(fault);
		}
	}
	public TestCase[] getFailures() {
		return failures;
	}
	public TestCase[] getPassedTCs() {
		return passedTCs;
	}
	public Set<Fault> getFaults() {
		return faults;
	}
}
