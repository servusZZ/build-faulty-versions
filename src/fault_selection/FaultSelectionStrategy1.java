package fault_selection;

import java.util.List;
import java.util.Set;

import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

/**
 * Select faults by random, with configs:<br>
 * 	- only one Fault per failure allowed<br>
 *  - pick faults in a way that some failures have 2 underlying faults
 */
public class FaultSelectionStrategy1 extends PitFaultSelectionStrategyBase{
	private final int VERSIONS_PER_FAULT_COUNT_2UNDERLYING_FAULTS = 2;
	public FaultSelectionStrategy1(int minFaultCount, int maxFaultCount, int versionsPerFaultCount) {
		super(minFaultCount, maxFaultCount, versionsPerFaultCount);
	}
	
	//		
	@Override
	public List<Set<PitMutation>> selectFaultyVersions(List<PitMutation> pitFaults, List<PitTestCase> pitTests) {
		FaultyVersionSelector selectionHelper = new FaultyVersionSelector();
		List<Set<PitMutation>> pitFaultyVersionsAll = selectionHelper.selectFaultyVersionsOneFaultPerFailure(minFaultCount, maxFaultCount, versionsPerFaultCount, pitFaults);
		int minFaultCountTwoFaults = minFaultCount;
		if (minFaultCountTwoFaults == 1) {
			minFaultCountTwoFaults = 2;
		}
		//TODO: für diese config nur 3 versions per fault count generieren
		pitFaultyVersionsAll.addAll(selectionHelper.selectFaultyVersionsTwoFaultsPerFailure(minFaultCountTwoFaults, maxFaultCount, VERSIONS_PER_FAULT_COUNT_2UNDERLYING_FAULTS, pitFaults));
//		pitFaultyVersionsAll.addAll(selectionHelper.selectFaultyVersionsFaultsCloseTogether());
		return pitFaultyVersionsAll;
	}
}
