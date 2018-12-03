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
		List<Set<PitMutation>> faultyVersionsPerFaultCount = new ArrayList<Set<PitMutation>>();
		/** If no version or just an identical version could be built
		 * for maxTries times in a row, the iteration stops.	*/
		int maxTries = 10, triesCount = 0;
		int maxTriesOnlyOneFailure = 20, triesCountOnlyOneFailure = 0;
		for (int k = 0; k < versionsPerFaultCount; k++) {
			while ((triesCount < maxTries) && (triesCountOnlyOneFailure < maxTriesOnlyOneFailure)) {
				Set<PitMutation> nextFaultyVersion = selectFaultyVersionTwoFaultsPerFailure(pitFaults, faultsCount);
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
	 * Constraint: All Failures must have maximum two underlying Faults.
	 * Selects all allowed faults by random.<br>
	 * Returns null, iff no faulty version could be built because the (by random) selected faults led to failures
	 * that excluded all remaining faults.
	 */
	private Set<PitMutation> selectFaultyVersionTwoFaultsPerFailure(List<PitMutation> pitFaults, int faultsCount){
		// select faults that only lead to one underlying fault first
		Set<PitMutation> faultyVersion = new HashSet<PitMutation>();
		Set<PitMutation> remainingFaults = new HashSet<PitMutation>(pitFaults);
		Set<PitMutation> twoUnderlyingFaults = new HashSet<PitMutation>();
		while (!remainingFaults.isEmpty()) {
			PitMutation nextFault = FaultsUtils.getRandomFault(remainingFaults);
			faultyVersion.add(nextFault);
			Set<PitMutation> tmpForbiddenFaults = FaultsUtils.getLinkedFaultsThroughFailures(nextFault);
			twoUnderlyingFaults.addAll(tmpForbiddenFaults);
			remainingFaults.removeAll(tmpForbiddenFaults);
			if (faultyVersion.size() == faultsCount-1) {
				break;
			}
		}
		// Out of the previous set of forbidden faults now pick faults by random.
		// They automatically cause some of the failures to have two underlying faults.
		twoUnderlyingFaults.removeAll(faultyVersion);
		while (faultyVersion.size() < faultsCount) {
			if (twoUnderlyingFaults.isEmpty()) {
				return null;
			}
			PitMutation nextFault = FaultsUtils.getRandomFault(twoUnderlyingFaults);
			faultyVersion.add(nextFault);
			twoUnderlyingFaults.removeAll(FaultsUtils.getLinkedFaultsThroughFailures(nextFault));
		}
		return faultyVersion;
	}
	public List<Set<PitMutation>> selectFaultyVersionsFaultsCloseTogether(){
		System.out.println("Faults Close Together Selection Strategy not yet implemented.");
		return new ArrayList<Set<PitMutation>>();
	}
}
