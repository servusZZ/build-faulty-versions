package fault_selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

/**
 * Class used for debugging Refinement step.
 * UnsupportedOperationException appeared, restore same fauly version.
 *
 */
public class SelectSpecificFaults1 extends PitFaultSelectionStrategyBase{
	public SelectSpecificFaults1() {
		super(3, 3, 1);
		
	}

	@Override
	public List<Set<PitMutation>> selectFaultyVersions(List<PitMutation> pitFaults, List<PitTestCase> pitTests) {
		List<Set<PitMutation>> faultyVersions = new ArrayList<Set<PitMutation>>();
		Set<PitMutation> faultyVersion = new HashSet<PitMutation>();
		String faultId1 = "org.biojava.nbio.structure.io.mmcif.model.AtomSite:setAuth_asym_id:(Ljava/lang/String;)V:void";
		String faultId2 = "org.biojava.nbio.ontology.obo.OboFileParser:findUnescaped:(Ljava/lang/String;CII)I:0";
		String faultId3 = "org.biojava.nbio.structure.rcsb.RCSBPolymer:getIndex:()Ljava/lang/Integer;:0";
		faultyVersion.add(getFaultById(pitFaults, faultId1));
		faultyVersion.add(getFaultById(pitFaults, faultId2));
		faultyVersion.add(getFaultById(pitFaults, faultId3));
		faultyVersions.add(faultyVersion);
		return faultyVersions;
	}

	private PitMutation getFaultById(List<PitMutation> pitFaults, String id) {
		for (PitMutation fault: pitFaults) {
			if (id.equals(fault.getId())) {
				return fault;
			}
		}
		return null;
	}
}
