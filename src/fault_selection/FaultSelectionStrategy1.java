package fault_selection;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import main.FaultyVersionsBuilder;
import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

/**
 * Select faults by random, with configs:<br>
 * 	- only one Fault per failure allowed<br>
 *  - pick faults in a way that some failures have 2 underlying faults
 */
public class FaultSelectionStrategy1 extends PitFaultSelectionStrategyBase{
	private final int VERSIONS_PER_FAULT_COUNT_2UNDERLYING_FAULTS = 3;
	public FaultSelectionStrategy1(int minFaultCount, int maxFaultCount, int versionsPerFaultCount,
			FaultyVersionsBuilder builder) {
		super(minFaultCount, maxFaultCount, versionsPerFaultCount, builder);
	}
	
	//		
	@Override
	public void selectAndProcessFaultyVersions(List<PitMutation> pitFaults, List<PitTestCase> pitTests) throws IOException {
		FaultyVersionSelector selectionHelper = new FaultyVersionSelector(this.builder);
		int nextFaultyVersionId = selectionHelper.processFaultyVersionsOneFaultPerFailure(minFaultCount, maxFaultCount, versionsPerFaultCount, pitFaults);
		int minFaultCountTwoFaults = minFaultCount;
		if (minFaultCountTwoFaults == 1) {
			minFaultCountTwoFaults = 2;
		}
		selectionHelper.processFaultyVersionsTwoFaultsPerFailure(minFaultCountTwoFaults, maxFaultCount, VERSIONS_PER_FAULT_COUNT_2UNDERLYING_FAULTS, pitFaults, nextFaultyVersionId);
	}
}
