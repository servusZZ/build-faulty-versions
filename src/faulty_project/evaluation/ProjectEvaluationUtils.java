package faulty_project.evaluation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import faulty_project.globals.FaultyProjectGlobals;
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
	/**
	 * Calculates the test suite metric DDU
	 * @return double[] {normalizedDensity, diversity, uniqueness, DDU}
	 */
	public static double[] calculateDDU(TestCase[] failures, TestCase[] passedTCs) {
		double normalizedDensity = calculateNormalizedDensity(failures, passedTCs);
		double diversity = calculateDiversity(failures, passedTCs);
		double uniqueness = calculateUniqueness(failures, passedTCs);
		return new double[] {normalizedDensity, diversity, uniqueness, normalizedDensity * uniqueness * diversity};
	}
	/**
	 * U = |G| / M
	 * G := ambiguityGroups, i.e. methods that are covered by exactly the same tests.
	 */
	private static double calculateUniqueness(TestCase[] failures, TestCase[] passedTCs) {
		// collect groups of methods with the same test-wise coverage
		Set<Set<Integer>> ambiguityGroups = new HashSet<Set<Integer>>();
		for (int methodID = 0; methodID < FaultyProjectGlobals.methodsCount; methodID++) {
			Set<Integer> testWiseCoverage = new HashSet<Integer>();
			for (int failureID = 0; failureID < failures.length; failureID++) {
				if (failures[failureID].coveredMethods.contains(methodID)) {
					testWiseCoverage.add(failureID);
				}
			}
			for (int j = 0; j < passedTCs.length; j++) {
				int passingTCId = failures.length + j;
				if (passedTCs[j].coveredMethods.contains(methodID)) {
					testWiseCoverage.add(passingTCId);
				}
			}
			ambiguityGroups.add(testWiseCoverage);
		}
		return (double)(ambiguityGroups.size()) / FaultyProjectGlobals.methodsCount;
	}
	/**
	 * G = 1 - (SIGMA(n * (n-1)) / N * (N -1)
	 * 	n := number of tests that share the same activity (equal covered Methods).
	 *  SIGMA builds the sum over all groups of activity patterns.
	 */
	private static double calculateDiversity(TestCase[] failures, TestCase[] passedTCs) {
		Map<Set<Integer>, Integer> testsWithSameActivityCounts = new HashMap<Set<Integer>, Integer>();
		for (int i = 0; i < FaultyProjectGlobals.failuresCount; i++) {
			testsWithSameActivityCounts.put(failures[i].coveredMethods, testsWithSameActivityCounts.getOrDefault(failures[i].coveredMethods, 0) + 1);
		}
		for (int i = 0; i < FaultyProjectGlobals.passedTestsCount; i++) {
			testsWithSameActivityCounts.put(passedTCs[i].coveredMethods, testsWithSameActivityCounts.getOrDefault(passedTCs[i].coveredMethods, 0) + 1);
		}
		int sum = 0;
		for (Integer testCount: testsWithSameActivityCounts.values()) {
			sum += testCount * (testCount-1);
		}
		return 1 - ((double)sum / (FaultyProjectGlobals.testsCount * (FaultyProjectGlobals.testsCount-1)));
	}
	/**
	 * returns the normalized density.
	 * The worst values of 0 and 1 of the density and the optimal value of 0.5 are normalized,
	 * so that 0 is the worst and 1 is the best value.
	 */
	private static double calculateNormalizedDensity(TestCase[] failures, TestCase[] passedTCs) {
		double density = calculateDensity(failures, passedTCs);
		return 1 - Math.abs(1 - 2 * density);
	}
	/**
	 * calculates the matrix density, the optimal value is 0.5
	 */
	private static double calculateDensity(TestCase[] failures, TestCase[] passedTCs) {
		int coverageSum = 0;
		for (int i = 0; i < failures.length; i++) {
			coverageSum += failures[i].coveredMethods.size();
		}
		for (int i = 0; i < passedTCs.length; i++) {
			coverageSum += passedTCs[i].coveredMethods.size();
		}
		return (double)coverageSum / (FaultyProjectGlobals.testsCount * FaultyProjectGlobals.methodsCount);
	}
}
