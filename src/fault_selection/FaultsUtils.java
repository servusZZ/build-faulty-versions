package fault_selection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

public class FaultsUtils {
	/**
	 * Receives all Failures of the passed faults and returns all faults that are linked to these failures.
	 * The passed fault is included in the returned set.
	 */
	public static Set<PitMutation> getLinkedFaultsThroughFailures(PitMutation fault){
		Set<PitMutation> linkedFaults = new HashSet<PitMutation>();
		Set<PitMutation> newLinkedFaults = new HashSet<PitMutation>();
		newLinkedFaults.add(fault);
		while (!newLinkedFaults.isEmpty()) {
			linkedFaults.addAll(newLinkedFaults);
			Set<PitTestCase> linkedTestsOfNewFaults = getLinkedTests(newLinkedFaults);
			newLinkedFaults = getLinkedFaults(linkedTestsOfNewFaults);
			newLinkedFaults.removeAll(linkedFaults);
		}
		return linkedFaults;
	}
	/**
	 * Returns all PIT Test Cases that are linked to the passed Faults.
	 */
	private static Set<PitTestCase> getLinkedTests(Set<PitMutation> newLinkedFaults){
		Set<PitTestCase> linkedTests = new HashSet<PitTestCase>();
		for (PitMutation linkeFault: newLinkedFaults) {
			linkedTests.addAll(linkeFault.getKillingTests());
		}
		return linkedTests;
	}
	/**
	 * Returns all Pit Mutations that are linked to the passed Tests.
	 */
	private static Set<PitMutation> getLinkedFaults(Set<PitTestCase> newLinkedTests){
		Set<PitMutation> linkedFaults = new HashSet<PitMutation>();
		for (PitTestCase linkedTest: newLinkedTests) {
			linkedFaults.addAll(linkedTest.getPossibleFaults());
		}
		return linkedFaults;
	}
	/**
	 * Returns an arbitrary fault of the passed set by random.
	 */
	public static PitMutation getRandomFault(Set<PitMutation> faults) {
		int i = getRandomFaultIndex(faults);
		int j = 0;
		for (PitMutation fault:faults) {
			if (i == j) {
				return fault;
			}
			j++;
		}
		return null;
	}
	private static int getRandomFaultIndex(Collection<PitMutation> pitFaults) {
		return (int) (Math.random() * pitFaults.size());
	}
}
