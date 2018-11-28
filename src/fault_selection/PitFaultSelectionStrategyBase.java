package fault_selection;

import java.util.List;
import java.util.Set;

import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

/**
 * Builds faulty version for a pit project that should be analyzed.
 * A faulty version means to select a subset of faults out of all faults of the project.
 */
public abstract class PitFaultSelectionStrategyBase {
	protected int minFaultCount;
	protected int maxFaultCount;
	protected int versionsPerFaultCount;
	public PitFaultSelectionStrategyBase(int minFaultCount,
			int maxFaultCount, int versionsPerFaultCount) {
		this.minFaultCount = minFaultCount;
		this.maxFaultCount = maxFaultCount;
		this.versionsPerFaultCount = versionsPerFaultCount;
	}
	/**
	 * Selects versionsPerFaultCount * (maxFaultCount - minFaultCount + 1) faulty versions
	 * according to the concrete strategy.
	 */
	public abstract List<Set<PitMutation>> selectFaultyVersions(List<PitMutation> pitFaults, List<PitTestCase> pitTests);
}
