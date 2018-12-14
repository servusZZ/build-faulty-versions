package fault_selection;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import main.FaultyVersionsBuilder;
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
	protected FaultyVersionsBuilder builder;
	public PitFaultSelectionStrategyBase(int minFaultCount,
			int maxFaultCount, int versionsPerFaultCount, FaultyVersionsBuilder builder) {
		this.minFaultCount = minFaultCount;
		this.maxFaultCount = maxFaultCount;
		this.versionsPerFaultCount = versionsPerFaultCount;
		this.builder = builder;
	}
	/**
	 * Selects versionsPerFaultCount * (maxFaultCount - minFaultCount + 1) faulty versions
	 * according to the concrete strategy.
	 */
	public abstract void selectAndProcessFaultyVersions(List<PitMutation> pitFaults, List<PitTestCase> pitTests) throws IOException;
}
