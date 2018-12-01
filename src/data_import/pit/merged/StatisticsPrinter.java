package data_import.pit.merged;

import java.util.ArrayList;
import java.util.List;

import pit.data_objects.EPitMutationStatus;
import pit.data_objects.PitMethod;
import pit.data_objects.PitMutation;
import pit.data_objects.PitTestCase;

public class StatisticsPrinter {
	private List<PitMethod> methods;
	private List<PitTestCase> tests;
	private final int XXL_TESTS_THRESHOLD = 100;
	private final int XL_TESTS_THRESHOLD = 60;
	private final int L_TESTS_THRESHOLD = 40;
	private final int M_TESTS_THRESHOLD = 20;
	
	private final int XL_FAULT_THRESHOLD = 20;
	private final int L_FAULT_THRESHOLD = 10;
	private final int M_FAULT_THRESHOLD = 5;
	
	public StatisticsPrinter(List<PitMethod> methods, List<PitTestCase> tests) {
		this.methods = methods;
		this.tests = tests;
	}
	/**
	 * Prints information about: How many tests cover how many methods,
	 * and how many underlying possible faults do these tests have
	 * and how many failures do these underlying faults cause (distribution and average).
	 */
	public void printTestStatistics() {
		System.out.println("DEBUG: Imported " + methods.size() + " methods.");
    	System.out.println("DEBUG: Imported " + tests.size() + " tests.");
    	int smallTests = 0;
    	int xlFaults = 0, lFaults = 0, mFaults = 0, sFaults = 0;
    	List<PitTestCase> xxl_tests, xl_tests, l_tests, m_tests;
    	List<Integer> xxl_tests_faultsDist, xl_tests_faultsDist, l_tests_faultsDist, m_tests_faultsDist;
    	xxl_tests = new ArrayList<PitTestCase>();
    	xl_tests = new ArrayList<PitTestCase>();
    	l_tests = new ArrayList<PitTestCase>();
    	m_tests = new ArrayList<PitTestCase>();
    	xxl_tests_faultsDist = new ArrayList<Integer>();
    	xl_tests_faultsDist = new ArrayList<Integer>();
    	l_tests_faultsDist = new ArrayList<Integer>();
    	m_tests_faultsDist = new ArrayList<Integer>();
    	int xxlTestsXLFaults = 0, xxlTestsLFaults = 0, xxlTestsMFaults = 0, xxlTestsSFaults = 0;
    	int xlTestsXLFaults = 0, xlTestsLFaults = 0, xlTestsMFaults = 0, xlTestsSFaults = 0;
    	int lTestsXLFaults = 0, lTestsLFaults = 0, lTestsMFaults = 0, lTestsSFaults = 0;
    	int mTestsXLFaults = 0, mTestsLFaults = 0, mTestsMFaults = 0, mTestsSFaults = 0;
    	
    	for (PitTestCase test: tests) {
    		xlFaults = 0; lFaults = 0; mFaults = 0; sFaults = 0;
    		if (test.getCoveredMethods().size() < M_TESTS_THRESHOLD) {
    			smallTests++;
    			continue;
    		}
    		for (PitMutation fault:test.getPossibleFaults()) {
    			if (fault.getKillingTests().size() < M_FAULT_THRESHOLD) {
    				sFaults++;
    				continue;
    			}
    			if (fault.getKillingTests().size() >= M_FAULT_THRESHOLD) {
    				mFaults++;
    			}
    			if (fault.getKillingTests().size() >= L_FAULT_THRESHOLD) {
    				lFaults++;
    			}
    			if (fault.getKillingTests().size() >= XL_FAULT_THRESHOLD) {
    				xlFaults++;
    			}
    		}
    		if (test.getCoveredMethods().size() >= M_TESTS_THRESHOLD) {
    			m_tests.add(test);
    			mTestsXLFaults += xlFaults;
    			mTestsLFaults += lFaults;
    			mTestsMFaults += mFaults;
    			mTestsSFaults += sFaults;
    			m_tests_faultsDist.add(test.getPossibleFaults().size());
    		}
    		if (test.getCoveredMethods().size() >= L_TESTS_THRESHOLD) {
    			l_tests.add(test);
    			lTestsXLFaults += xlFaults;
    			lTestsLFaults += lFaults;
    			lTestsMFaults += mFaults;
    			lTestsSFaults += sFaults;
    			l_tests_faultsDist.add(test.getPossibleFaults().size());
    		}
    		if (test.getCoveredMethods().size() >= XL_TESTS_THRESHOLD) {
    			xl_tests.add(test);
    			xlTestsXLFaults += xlFaults;
    			xlTestsLFaults += lFaults;
    			xlTestsMFaults += mFaults;
    			xlTestsSFaults += sFaults;
    			xl_tests_faultsDist.add(test.getPossibleFaults().size());
    		}
    		if (test.getCoveredMethods().size() >= XXL_TESTS_THRESHOLD) {
    			xxl_tests.add(test);
    			xxlTestsXLFaults += xlFaults;
    			xxlTestsLFaults += lFaults;
    			xxlTestsMFaults += mFaults;
    			xxlTestsSFaults += sFaults;
    			xxl_tests_faultsDist.add(test.getPossibleFaults().size());
    		}
    	}
    	{ // print block
    	System.out.println("" + xxl_tests.size() + " tests cover at least " + XXL_TESTS_THRESHOLD + " methods.");
    	if (xxl_tests.size() > 0) {
    		System.out.println("\t\t" + xxlTestsXLFaults + " faults cause at least " + XL_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + xxlTestsLFaults + " faults cause at least " + L_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + xxlTestsMFaults + " faults cause at least " + M_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + xxlTestsSFaults + " faults cause less than " + M_FAULT_THRESHOLD + " failures.");
    		// Distribution of the number of underlying possible faults per Failure
    		System.out.println("\tA Failure has on average " + (((double)(xxlTestsMFaults + xxlTestsSFaults))/xxl_tests.size()) + " underlying possible faults.");
    		System.out.println("\t" + xxl_tests_faultsDist);
    	}
    	System.out.println("" + xl_tests.size() + " tests cover at least " + XL_TESTS_THRESHOLD + " methods.");
    	if (xl_tests.size() > 0) {
    		System.out.println("\t\t" + xlTestsXLFaults + " faults cause at least " + XL_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + xlTestsLFaults + " faults cause at least " + L_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + xlTestsMFaults + " faults cause at least " + M_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + xlTestsSFaults + " faults cause less than " + M_FAULT_THRESHOLD + " failures.");
    		// Distribution of the number of underlying possible faults per Failure
    		System.out.println("\tA Failure has on average " + (((double)(xlTestsMFaults + xlTestsSFaults))/xl_tests.size()) + " underlying possible faults.");
    		System.out.println("\t" + xl_tests_faultsDist);
    	}
    	System.out.println("" + l_tests.size() + " tests cover at least " + L_TESTS_THRESHOLD + " methods.");
    	if (l_tests.size() > 0) {
    		System.out.println("\t\t" + lTestsXLFaults + " faults cause at least " + XL_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + lTestsLFaults + " faults cause at least " + L_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + lTestsMFaults + " faults cause at least " + M_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + lTestsSFaults + " faults cause less than " + M_FAULT_THRESHOLD + " failures.");
    		// Distribution of the number of underlying possible faults per Failure
    		System.out.println("\tA Failure has on average " + (((double)(lTestsMFaults + lTestsSFaults))/l_tests.size()) + " underlying possible faults.");
    		System.out.println("\t" + l_tests_faultsDist);
    	}
    	System.out.println("" + m_tests.size() + " tests cover at least " + M_TESTS_THRESHOLD + " methods.");
    	if (m_tests.size() > 0) {
    		System.out.println("\t\t" + mTestsXLFaults + " faults cause at least " + XL_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + mTestsLFaults + " faults cause at least " + L_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + mTestsMFaults + " faults cause at least " + M_FAULT_THRESHOLD + " failures.");
    		System.out.println("\t\t" + mTestsSFaults + " faults cause less than " + M_FAULT_THRESHOLD + " failures.");
    		// Distribution of the number of underlying possible faults per Failure
    		System.out.println("\tA Failure has on average " + (((double)(mTestsMFaults + mTestsSFaults))/m_tests.size()) + " underlying possible faults.");
    		System.out.println("\t" + m_tests_faultsDist);
    	}
    	System.out.println("" + smallTests + " tests cover less than " + M_TESTS_THRESHOLD + " methods.");
    	}
    }
	/**
	 * not needed.
	 */
	public void printMethodStatistics() {
		System.out.println("DEBUG: Imported " + methods.size() + " methods.");
		int xxl = 0, xl = 0, m = 0, s = 0;
		int fault_xl = 0, fault_l = 0, fault_m = 0, fault_s = 0;
    	for (PitMethod method: methods) {
    		if (method.getCoveringTests().size() < M_TESTS_THRESHOLD) {
    			s++;
    		}
    		if (method.getCoveringTests().size() >= M_TESTS_THRESHOLD) {
    			m++;
    		}
    		if (method.getCoveringTests().size() >= XL_TESTS_THRESHOLD) {
    			xl++;
    		}
    		if (method.getCoveringTests().size() >= XXL_TESTS_THRESHOLD) {
    			xxl++;
    		}
    		for (PitMutation fault: method.getMutations()) {
    			if (!(fault.getStatus().equals(EPitMutationStatus.KILLED))) {
    				continue;
    			}
    			if (fault.getKillingTests().size() < M_FAULT_THRESHOLD) {
    				fault_s++;
    				continue;
    			}
    			if (fault.getKillingTests().size() >= M_FAULT_THRESHOLD) {
    				fault_m++;
    			}
    			if (fault.getKillingTests().size() >= L_FAULT_THRESHOLD) {
    				fault_l++;
    			}
    			if (fault.getKillingTests().size() >= XL_FAULT_THRESHOLD) {
    				fault_xl++;
    			}
    		}
    	}
    	System.out.println("DEBUG: " + xxl + " methods are covered by at least " + XXL_TESTS_THRESHOLD + " tests.");
    	System.out.println("DEBUG: " + xl + " methods cover at least " + XL_TESTS_THRESHOLD + " tests.");
    	System.out.println("DEBUG: " + m + " methods cover at least " + M_TESTS_THRESHOLD + " tests.");
    	System.out.println("DEBUG: " + s + " methods cover less than " + M_TESTS_THRESHOLD + " tests.");
    	
    	System.out.println("DEBUG: " + fault_xl + " faults cause at least " + XL_FAULT_THRESHOLD + " failures.");
    	System.out.println("DEBUG: " + fault_l + " faults cause at least " + L_FAULT_THRESHOLD + " failures.");
    	System.out.println("DEBUG: " + fault_m + " faults cause at least " + M_FAULT_THRESHOLD + " failures.");
    	System.out.println("DEBUG: " + fault_s + " faults cause less than " + M_FAULT_THRESHOLD + " failures.");
	}
}
