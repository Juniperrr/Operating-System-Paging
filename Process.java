package Lab4;

import java.util.Scanner;

public class Process {
	int processSize;
	int nextWord;
	
	//stats related
	int numPageFaults;
	int evictTime;
	int totalResidencyTime;
	
	public Process (int processID, int processSize) {
		this.processSize = processSize;
		this.nextWord = (111 * processID) % processSize;
		this.numPageFaults = 0;
		this.evictTime = 0;
		this.totalResidencyTime = 0;
	}
	
	//Add total resident time
	public void addResidency(int time) {
		totalResidencyTime += time;
	}
	//Get the next reference word
	public int getNextWord() {
		return nextWord;
	}
	
	//Add page fault, which means cannot find the page in the frame page time by one
	public void increasePageFaults() {
		numPageFaults++;
	}
	public void increaseEvictTimes() {
		evictTime++;
	}
	//Compute the next reference word by A, B, and C
	public void setNextReference(double A, double B, double C, Scanner randomScanner) {
		int randomNum = randomScanner.nextInt();
		
		double quotient = randomNum / (Integer.MAX_VALUE + 1d);
		if (quotient < A) {
			nextWord = (nextWord + 1) % processSize;	
		}
		else if (quotient < A + B) {
			nextWord = (nextWord - 5 + processSize) % processSize;
		}
		else if (quotient < A + B + C) {
			nextWord = (nextWord + 4) % processSize;
		}
		else {
			int randomRef = randomScanner.nextInt() % processSize;
			nextWord = randomRef;
		}
	}
}
