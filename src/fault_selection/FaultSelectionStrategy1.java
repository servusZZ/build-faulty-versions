package fault_selection;

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
	
	//TODO: 2 Faults per Failure erlauben - selection implementieren,
	//				in projekt metrics speichern wie viele der Failures mehrere Faults haben
	//				zudem darauf achten, ob faults in der gleichen Klassen vorkommen (oder hierfür nur wenige versionen manuell erstellen?)
	//		
	@Override
	public List<Set<PitMutation>> selectFaultyVersions(List<PitMutation> pitFaults, List<PitTestCase> pitTests) {
		FaultyVersionSelector selectionHelper = new FaultyVersionSelector();
		List<Set<PitMutation>> pitFaultyVersionsAll = selectionHelper.selectFaultyVersionsOneFaultPerFailure(minFaultCount, maxFaultCount, versionsPerFaultCount, pitFaults);
		int minFaultCountTwoFaults = minFaultCount;
		if (minFaultCountTwoFaults == 1) {
			minFaultCountTwoFaults = 2;
		}
		pitFaultyVersionsAll.addAll(selectionHelper.selectFaultyVersionsTwoFaultsPerFailure(minFaultCountTwoFaults, maxFaultCount, versionsPerFaultCount, pitFaults));
		pitFaultyVersionsAll.addAll(selectionHelper.selectFaultyVersionsFaultsCloseTogether());
		return pitFaultyVersionsAll;
	}
}
