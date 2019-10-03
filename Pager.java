package Lab4;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Pager {
	static Process[] processes;
	static FrameReplacementPolicy frameTablePolicy;
	
	static int machineSize;
	static int pageSize;
	static int processSize;
	static int jobMix;
	static int numRef;
	static String replacementAlgorithm;
	static int debuggingLevel;
	static Scanner randNumScanner;
	
	static final int QUANTUM = 3;
	
	public static void main(String[] args) throws FileNotFoundException {
		//Read program Arguments
		machineSize = Integer.parseInt(args[0]);
		pageSize = Integer.parseInt(args[1]);
		processSize = Integer.parseInt(args[2]);
		jobMix = Integer.parseInt(args[3]);
		numRef = Integer.parseInt(args[4]);
		replacementAlgorithm = args[5];
		debuggingLevel = Integer.parseInt(args[6]);
		randNumScanner = new Scanner(new FileReader("random-numbers.txt"));
		
		if (replacementAlgorithm.equalsIgnoreCase("FIFO")) {
			frameTablePolicy = new FramePolicyFIFO(machineSize, pageSize);
		
		}
		else if (replacementAlgorithm.equalsIgnoreCase("LRU")) {
			frameTablePolicy = new FramePolicyLRU(machineSize, pageSize);
		}
		
		else if (replacementAlgorithm.equalsIgnoreCase("RANDOM")) {
			frameTablePolicy = new FramePolicyRANDOM(machineSize, pageSize, randNumScanner);
		}
		else {
			System.out.println("ERROR INPUT FORMAT!");
		}
		
		runPolicy();
		printStats();
		
	}
	
	/*run by the job mix number differently*/
	public static void runPolicy () {
		if (jobMix == 1) {
			int processNumber = 1;
			processes = new Process[1];
			processes[0] = new Process(processNumber, processSize);
			runPagerForOneProcess();
			//jobMixEquals1(); FM
		}
		//different conditions, the function differs by the jobMix
		else {//jobMix could be 2, 3, 4
			processes = new Process[4];
			for (int i = 0; i < 4; i++) {
				processes[i] = new Process(i+1, processSize);
			}
			runPagerForFourProcess(jobMix);
		}
	}
	
	private static void runPagerForOneProcess() {
		int procId = 1;
		for (int cycle = 1; cycle <= numRef; cycle++) {
			int pageNumber = processes[0].getNextWord() / pageSize;
			
			//if has page fault then replace
			if (frameTablePolicy.pageFaultOccured(pageNumber, procId, cycle)) {
				frameTablePolicy.replacePage(processes, pageNumber, procId, cycle);
				processes[0].increasePageFaults();
			}
			//change to the next reference word
			processes[0].setNextReference(procId, 0, 0, randNumScanner);
		}
	}
	
	private static void runPagerForFourProcess(int jobMix) {
		int totalCycle = numRef / QUANTUM;
		double A[] = new double[4];
		double B[] = new double[4];
		double C[] = new double[4];
		
		//Initiate A, B, B for each process
		//jobMix is 2: a = 1, b = 0 = c
		if (jobMix ==2) {
			for (int i = 0; i < 4; i++) {
				A[i] = 1;
				B[i] = 0;
				C[i] = 0;
			}
		}
		//jobMix is 3: a = b = c = 0
		else if (jobMix ==3) {
			for (int i = 0; i < 4; i++) {
				A[i] = 0;
				B[i] = 0;
				C[i] = 0;
			}
		}
		//jobMix is 4: A, B, C differs
		else if (jobMix ==4) {
			A[0] = 0.75;		B[0] = 0.25;		C[0] = 0;
			A[1] = 0.75;		B[1] = 0;			C[1] = 0.25;
			A[2] = 0.75;		B[2] = 0.125;		C[2] = 0.125;
			A[3] = 0.75;		B[3] = 0.125;		C[3] = 0.125;
		}
		
		//run all processes, each process begins referencing by its A,B,C
		for (int cycle = 0; cycle <= totalCycle; cycle++) {
			for (int procNum = 0; procNum < 4; procNum++) {
				runProcess(procNum+1, A[procNum], B[procNum], C[procNum], cycle, totalCycle);
			}
		}
	}
	private static void runProcess(int procNum, double A, double B, double C, int cycle, int totalCycle) {
		int referenceTime; //how many times wil produce a reference owrd in one quantum
		//if is not the final cycle then run a full quantum
		if (cycle != totalCycle) {
			referenceTime = QUANTUM;
		}
		//if is the final then run remaining reference time
		else {
			referenceTime = numRef % QUANTUM;
		}
		//a process starts referencing
		for (int ref = 0; ref < referenceTime; ref++) {
			
			int time = QUANTUM * cycle * 4 + ref + 1 + (procNum - 1) * referenceTime;
			
			int pageNumber = processes[procNum - 1].getNextWord() / pageSize;
			//if has page fault
			if (frameTablePolicy.pageFaultOccured(pageNumber, procNum, time)) {
				frameTablePolicy.replacePage(processes, pageNumber, procNum, time);
				processes[procNum - 1].increasePageFaults();
			}
			//referencinig the next word.
			
			processes[procNum - 1].setNextReference(A, B, C, randNumScanner);
		}
	}

	public static void printStats() {
		int totalFaultTimes = 0;
		int totalResidencyTimes = 0;
		int totalEvictTimes = 0;
		
		//show inputting data from the beginnning of the program
		System.out.println("The machine size is " + machineSize);
		System.out.println("The page size is " + pageSize);
		System.out.println("The process size is " + processSize);
		System.out.println("The job mix number is " + jobMix);
		System.out.println("The number of references per process  is " + numRef);
		System.out.println("The replacement algorithm  is " + replacementAlgorithm);
		System.out.println("The level of debugging  is " + debuggingLevel + "\n");
		
		for (int i = 0; i <processes.length; i++) {
			int faultTime = processes[i].numPageFaults;
			int residencyTime = processes[i].totalResidencyTime;
			int evictTime = processes[i].evictTime;
			
			if (evictTime == 0) {
				System.out.println("Process " + (i + 1) + " had " + faultTime + " faults. \n\tWith no evictions, the average residence is undefined.");
			
			}
			else {
				double averageResidency = (double) residencyTime / evictTime;
				System.out.println("Process " + (i + 1) + " had " + faultTime + " faults and " + averageResidency + " average residency.");
			}
			
			totalFaultTimes += faultTime;
			totalResidencyTimes += residencyTime;
			totalEvictTimes += evictTime;
		}
		
		//total number rof page faults and the overall average resident time
		if (totalEvictTimes == 0) {
			 System.out.println("\n the total number of faults is " + totalFaultTimes + ".\n\tWith no evictions, the overrall average ersidency is undefined.");
			 
		}
		else {
			double totalAverageResidency = (double) totalResidencyTimes / totalEvictTimes;
			System.out.println("\nThe total number of faults is " + totalFaultTimes + " and the overall average residency is " + totalAverageResidency);
		}
	}
}
