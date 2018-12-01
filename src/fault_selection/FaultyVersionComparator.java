package fault_selection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

public class FaultyVersionComparator {
	/**
	 * Returns true, iff the faults in the 2 versions lead to exactly the same failures.
	 */
	private boolean failuresAreEqual(Set<PitMutation> faultyVersion1, Set<PitMutation> faultyVersion2) {
		Set<PitTestCase> failures1 = getFailuresOfFaultyVersion(faultyVersion1);
		Set<PitTestCase> failures2 = getFailuresOfFaultyVersion(faultyVersion2);
		return failures1.equals(failures2);
	}
	private Set<PitTestCase> getFailuresOfFaultyVersion(final Set<PitMutation> faultyVersion){
		Set<PitTestCase> failures = new HashSet<PitTestCase>();
		for (PitMutation fault: faultyVersion) {
			failures.addAll(fault.getKillingTests());
		}
		return failures;
	}
	/**
	 * True, if a version that contains exactly the same faults
	 *   OR a version that contains exactly the same failures is already contained in faultyVersions.
	 */
	public boolean faultyVersionIsAlreadyContained(List<Set<PitMutation>> faultyVersions,
			Set<PitMutation> newFaultyVersion) {
		for (Set<PitMutation> faultyVersion: faultyVersions) {
			if (faultyVersion.equals(newFaultyVersion)
					|| failuresAreEqual(faultyVersion, newFaultyVersion)) {
				return true;
			}
		}
		return false;
	}
	public boolean versionContainsOnlyOneFailure(Set<PitMutation> faultyVersion) {
		PitTestCase firstFailure = faultyVersion.iterator().next().getKillingTests().iterator().next();
		for (PitMutation fault:faultyVersion) {
			if (fault.getKillingTests().size() > 1) {
				return false;
			}
			if (!(firstFailure == fault.getKillingTests().iterator().next())) {
				return false;
			}
		}
		return true;
	}
}
