package fault_selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

/**
 * Select faults by random and only one fault per Failure allowed.
 * Used for testing the whole prioritization pipeline.
 */
public class FaultSelectionStrategy1 extends PitFaultSelectionStrategyBase{
	
	public FaultSelectionStrategy1(int minFaultCount, int maxFaultCount, int versionsPerFaultCount) {
		super(minFaultCount, maxFaultCount, versionsPerFaultCount);
	}
	
	/**
	 * Select faults by random and only one fault per Failure allowed.
	 * It can happen that a faultyVersion can't be built due to this constraint.
	 * @param pitTests not used in this fault selection implementation.
	 */
	public List<Set<PitMutation>> selectFaultyVersions(List<PitMutation> pitFaults, List<PitTestCase> pitTests) {
		List<Set<PitMutation>> faultyVersions = new ArrayList<Set<PitMutation>>();
		/**	If no faulty version could be built, a new try starts */
		int maxTries = 5, triesCount = 0;
		for (int faultsCount = minFaultCount; faultsCount <= maxFaultCount; faultsCount++) {
			for (int k = 0; k < versionsPerFaultCount; k++) {
				while (triesCount < maxTries) {
					Set<PitMutation> nextFaultyVersion = selectFaultyVersion(pitFaults, faultsCount);
					if (nextFaultyVersion != null) {
						faultyVersions.add(nextFaultyVersion);
						break;
					}
					triesCount++;
				}
			}
		}
		return faultyVersions;
	}
	/**
	 * Returns a faulty version with the passed number of faults.
	 * Constraint: All Failures must have only one underlying Fault.
	 * Selects all allowed faults by random.<br>
	 * Returns null, iff no faulty version could be built because the (by random) selected faults led to failures
	 * that excluded all remaining faults.
	 */
	private Set<PitMutation> selectFaultyVersion(List<PitMutation> pitFaults, int faultsCount){
		Set<PitMutation> faultyVersion = new HashSet<PitMutation>();
		Set<PitMutation> remainingFaults = new HashSet<>(pitFaults);
		while (!remainingFaults.isEmpty()) {
			PitMutation nextFault = getRandomFault(remainingFaults);
			faultyVersion.add(nextFault);
			remainingFaults.removeAll(getLinkedFaultsThroughFailures(nextFault));
			if (faultyVersion.size() == faultsCount) {
				return faultyVersion;
			}
		}
		return null;
	}
	/**
	 * Receives all Failures of the passed faults and returns all faults that are linked to these failures.
	 * The passed fault is included in the returned set.
	 */
	private Set<PitMutation> getLinkedFaultsThroughFailures(PitMutation fault){
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
	private Set<PitTestCase> getLinkedTests(Set<PitMutation> newLinkedFaults){
		Set<PitTestCase> linkedTests = new HashSet<PitTestCase>();
		for (PitMutation linkeFault: newLinkedFaults) {
			linkedTests.addAll(linkeFault.getKillingTests());
		}
		return linkedTests;
	}
	/**
	 * Returns all Pit Mutations that are linked to the passed Tests.
	 */
	private Set<PitMutation> getLinkedFaults(Set<PitTestCase> newLinkedTests){
		Set<PitMutation> linkedFaults = new HashSet<PitMutation>();
		for (PitTestCase linkedTest: newLinkedTests) {
			linkedFaults.addAll(linkedTest.getPossibleFaults());
		}
		return linkedFaults;
	}
	/**
	 * Returns a fault by random which is not already contained in the passed set.<br>
	 * Unused
	 */
	private PitMutation getRandomUniqueFault(Set<PitMutation> alreadySelectedFaults, List<PitMutation> pitFaults) {
		PitMutation fault = pitFaults.get(getRandomFaultIndex(pitFaults));
		while (alreadySelectedFaults.contains(fault)) {
			fault = pitFaults.get(getRandomFaultIndex(pitFaults));
		}
		return fault;
	}
	/**
	 * Returns an arbitrary fault of the passed set by random.
	 */
	private PitMutation getRandomFault(Set<PitMutation> faults) {
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
	private int getRandomFaultIndex(Collection<PitMutation> pitFaults) {
		return (int) (Math.random() * pitFaults.size());
	}
}
