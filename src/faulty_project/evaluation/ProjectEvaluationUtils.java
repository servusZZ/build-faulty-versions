package faulty_project.evaluation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import prioritization.data_objects.Fault;
import prioritization.data_objects.TestCase;

public class ProjectEvaluationUtils {
	public static double getMedianTestSize(TestCase[] failures) {
		Arrays.sort(failures);
		if (failures.length % 2 == 0) {
			return ((double)failures[failures.length/2].coveredMethods.size() + (double)failures[failures.length/2 - 1].coveredMethods.size())/2;
		}
		return failures[failures.length/2].coveredMethods.size();
	}
	
	public static double getAverageTestSize(TestCase[] failures) {
		int sumTestSizes = 0;
		for (int i = 0; i < failures.length; i++) {
			sumTestSizes += failures[i].coveredMethods.size();
		}
		return (double)sumTestSizes / (double)failures.length;
	}
	
	/**
	 * Counts how often two faults are located in the same class.<br>
	 * To get a ratio, this number must be divided by the total number of fault-pairs: n*(n-1) / 2
	 */
	public static int getFaultsInSameClassPairsCount(Set<Fault> faults) {
		int pairsCount = 0;
		Map<String, Integer> faultsPerClassCount = new HashMap<String, Integer>();
		for (Fault fault: faults) {
			faultsPerClassCount.put(fault.getClassName(), faultsPerClassCount.getOrDefault(fault.getClassName(), 0)+1);
		}
		for (int value: faultsPerClassCount.values()) {
			pairsCount += ((value * (value-1))/2);
		}
		return pairsCount;
	}
	/**
	 * Counts how often two faults are located in the same package.<br>
	 * To get a ratio, this number must be divided by the total number of fault-pairs: n*(n-1) / 2
	 */
	public static int getFaultsInSamePackagePairsCount(Set<Fault> faults) {
		int pairsCount = 0;
		Map<String, Integer> faultsPerPackageCount = new HashMap<String, Integer>();
		for (Fault fault: faults) {
			faultsPerPackageCount.put(fault.getPackageName(), faultsPerPackageCount.getOrDefault(fault.getPackageName(), 0)+1);
		}
		for (int value: faultsPerPackageCount.values()) {
			pairsCount += ((value * (value-1))/2);
		}
		return pairsCount;
	}
	/**
	 * Counts how many of the passed failures have at least 2 underlying faults.
	 */
	public static int getFailuresWithMultipleFaultsCount(TestCase[] failures) {
		int sum = 0;
		for (int i = 0; i < failures.length; i++) {
			if (failures[i].getFaults().size() > 1) {
				sum++;
			}
		}
		return sum;
	}
}
