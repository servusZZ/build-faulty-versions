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

public class PitDataObjectsConverter {
	private TestCase[] failures;
	private TestCase[] passedTCs;
	private Set<Fault> faults;
	/**	Maps each fault as PitMutation to the respective Fault object */
	private Map<PitMutation, Fault> pitFaultFaultMapping;
	private Map<PitMethod, Integer> methodIndexMapping;
	
	public PitDataObjectsConverter(Set<PitMutation> pitFaults, Set<PitTestCase> pitFailures,
			Set<PitTestCase> pitPassedTCs, Set<PitMethod> relevantMethods) {
		initFaults(pitFaults);
		initMethodIndexMapping(relevantMethods);
		failures = initFailures(pitFailures, pitFaults);
		passedTCs = initPassedTestCases(pitPassedTCs);
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
	 * Creates TestCase objects for the failed pit test cases.
	 * The methodIndexMapping must already be set before calling this method.
	 * Also sets the faults of each failure.
	 */
	private TestCase[] initFailures(Set<PitTestCase> pitTestCases, Set<PitMutation> pitFaults) {
		TestCase[] testCases = new TestCase[pitTestCases.size()];
		int i = 0;
		for (PitTestCase pitTestCase: pitTestCases) {
			Set<PitMutation> underlyingFaults = new HashSet<PitMutation>(pitTestCase.getPossibleFaults());
			underlyingFaults.retainAll(pitFaults);
			testCases[i] = new TestCase(pitTestCase.getName(), false, getMethodIndexes(pitTestCase.getCoveredMethods()));
			testCases[i].setFaults(getFaultsFromPitFaults(underlyingFaults));
			i++;
		}
		return testCases;
	}
	/**
	 * Returns the respective faults that were created from the passed pit faults.
	 * Uses the pitFault to Fault Mapping.
	 */
	private Set<Fault> getFaultsFromPitFaults(Set<PitMutation> pitFaults){
		Set<Fault> faults = new HashSet<Fault>();
		for (PitMutation pitFault: pitFaults) {
			faults.add(pitFaultFaultMapping.get(pitFault));
		}
		return faults;
	}
	/**
	 * Creates TestCase objects for the passed pit test cases.
	 * The methodIndexMapping must already be set before calling this method.
	 */
	private TestCase[] initPassedTestCases(Set<PitTestCase> pitTestCases) {
		TestCase[] testCases = new TestCase[pitTestCases.size()];
		int i = 0;
		for (PitTestCase pitTestCase: pitTestCases) {
			testCases[i] = new TestCase(pitTestCase.getName(), true, getMethodIndexes(pitTestCase.getCoveredMethods()));
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
	/**
	 * Creates Fault objects for each passed pitFault.
	 * Also initializes the pitFaultFaultMapping.
	 */
	private void initFaults(Set<PitMutation> pitFaults) {
		faults = new HashSet<Fault>();
		pitFaultFaultMapping = new HashMap<PitMutation, Fault>();
		for (PitMutation pitFault: pitFaults) {
			Fault fault = new Fault(pitFault.getId(), new ArrayList<String>(pitFault.getKillingTestsNames()));
			faults.add(fault);
			pitFaultFaultMapping.put(pitFault, fault);
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
