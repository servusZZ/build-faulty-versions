package fault_selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pit.data_objects.PitMutation;

public class FaultyVersionSelector {
	private FaultyVersionComparator comparator;
	public FaultyVersionSelector() {
		this.comparator = new FaultyVersionComparator();
	}
	public List<Set<PitMutation>> selectFaultyVersionsOneFaultPerFailure(int minFaultCount, int maxFaultCount, int versionsPerFaultCount, List<PitMutation> pitFaults){
		List<Set<PitMutation>> oneFaultPerFailureAll = new ArrayList<Set<PitMutation>>();
		List<Set<PitMutation>> oneFaultPerFailure;
		for (int faultsCount = minFaultCount; faultsCount <= maxFaultCount; faultsCount++) {
			oneFaultPerFailure = selectFaultyVersionsOneFaultPerFailure(faultsCount, versionsPerFaultCount, pitFaults);
			if (oneFaultPerFailure == null) {
				System.out.println("DEBUG: No version containing " + faultsCount + " faults could be built anymore. Config: Only One Fault per Failure.");
				break;
			}
			oneFaultPerFailureAll.addAll(oneFaultPerFailure);
		}
		return oneFaultPerFailureAll;
	}
	/**
	 * Builds faulty versions for the passed number of faults.
	 * Per Failure only one Fault is allowed.<br>
	 * Returns null, iff no faulty version could be built (by the number of defined maximum tries).
	 */
	private List<Set<PitMutation>> selectFaultyVersionsOneFaultPerFailure(int faultsCount, int versionsPerFaultCount, List<PitMutation> pitFaults){
		List<Set<PitMutation>> faultyVersionsPerFaultCount = new ArrayList<Set<PitMutation>>();
		/** If no version or just an identical version could be built
		 * for maxTries times in a row, the iteration stops.	*/
		int maxTries = 10, triesCount = 0;
		int maxTriesOnlyOneFailure = 20, triesCountOnlyOneFailure = 0;
		for (int k = 0; k < versionsPerFaultCount; k++) {
			while ((triesCount < maxTries) && (triesCountOnlyOneFailure < maxTriesOnlyOneFailure)) {
				Set<PitMutation> nextFaultyVersion = selectFaultyVersionOneFaultPerFailure(pitFaults, faultsCount);
				if (nextFaultyVersion == null
						|| comparator.faultyVersionIsAlreadyContained(faultyVersionsPerFaultCount, nextFaultyVersion)) {
					triesCount++;
				} 
				else if (comparator.versionContainsOnlyOneFailure(nextFaultyVersion)) {
					triesCountOnlyOneFailure++;
				}
				else { // a successful version could be built, reset tries counters and build the next version
					faultyVersionsPerFaultCount.add(nextFaultyVersion);
					triesCount = 0;
					triesCountOnlyOneFailure = 0;
					break;
				}
			}
		}
		if (faultyVersionsPerFaultCount.isEmpty()) {
			return null;
		}
		return faultyVersionsPerFaultCount;
	}
	/**
	 * Returns a faulty version with the passed number of faults.
	 * Constraint: All Failures must have only one underlying Fault.
	 * Selects all allowed faults by random.<br>
	 * Returns null, iff no faulty version could be built because the (by random) selected faults led to failures
	 * that excluded all remaining faults.
	 */
	private Set<PitMutation> selectFaultyVersionOneFaultPerFailure(List<PitMutation> pitFaults, int faultsCount){
		Set<PitMutation> faultyVersion = new HashSet<PitMutation>();
		Set<PitMutation> remainingFaults = new HashSet<>(pitFaults);
		while (!remainingFaults.isEmpty()) {
			PitMutation nextFault = FaultsUtils.getRandomFault(remainingFaults);
			faultyVersion.add(nextFault);
			remainingFaults.removeAll(FaultsUtils.getLinkedFaultsThroughFailures(nextFault));
			if (faultyVersion.size() == faultsCount) {
				return faultyVersion;
			}
		}
		return null;
	}
	
	public List<Set<PitMutation>> selectFaultyVersionsTwoFaultsPerFailure(int minFaultCount, int maxFaultCount, int versionsPerFaultCount, List<PitMutation> pitFaults){
		List<Set<PitMutation>> twoFaultsPerFailureAll = new ArrayList<Set<PitMutation>>();
		List<Set<PitMutation>> twoFaultsPerFailure;
		for (int faultsCount = minFaultCount; faultsCount <= maxFaultCount; faultsCount++) {
			twoFaultsPerFailure = selectFaultyVersionsTwoFaultsPerFailure(faultsCount, versionsPerFaultCount, pitFaults);
			if (twoFaultsPerFailure == null) {
				System.out.println("DEBUG: No version containing " + faultsCount + " faults could be built anymore. Config: Two Faults per Failure allowed.");
				break;
			}
			twoFaultsPerFailureAll.addAll(twoFaultsPerFailure);
		}
		return twoFaultsPerFailureAll;
	}
	/**
	 * Builds faulty versions for the passed number of faults.
	 * Per Failure only two Faults are allowed.<br>
	 * Returns null, iff no faulty version could be built (by the number of defined maximum tries).
	 */
	private List<Set<PitMutation>> selectFaultyVersionsTwoFaultsPerFailure(int faultsCount, int versionsPerFaultCount, List<PitMutation> pitFaults){
		System.out.println("Two Faults per Failure allowed Selection Strategy not yet implemented.");
		return null;
	}
	public List<Set<PitMutation>> selectFaultyVersionsFaultsCloseTogether(){
		System.out.println("Faults Close Together Selection Strategy not yet implemented.");
		return new ArrayList<Set<PitMutation>>();
	}
}
