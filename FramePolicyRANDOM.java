package Lab4;

import java.util.Scanner;

public class FramePolicyRANDOM implements FrameReplacementPolicy {
	Scanner random;
	int numFrames;
	int[][]frameTable; //each frame contains three information:page number, process this page belongs to and when this frame is put into memory
	
	public FramePolicyRANDOM(int machineSize, int pageSize, Scanner random) {
		this.numFrames = machineSize / pageSize;
		this.random = random;
		this.frameTable = new int[numFrames][3];
	}
	

	@Override
	public boolean pageFaultOccured(int pageNumber, int processNumber, int currentTime) {
		for (int i = 0; i < numFrames; i++) {
			//if the demanding page is in the frame table then obviously o page fault occurs
			if ((frameTable[i][0] == pageNumber) && frameTable[i][1] == processNumber) {
				return false;
			}
		}
		//Cannot find the demanding page, so page fault occurred.
		return true; 
	}

	@Override
	public void replacePage(Process[] processes, int pageNumber, int processNumber, int currentTime) {
		for (int i = (numFrames - 1); i >= 0; i--) {
			//if there was an unusedframe, use that frame element and end searching, searching begins from the highest address
			if ((frameTable[i][0] == 0) && (frameTable[i][1] == 0)) {
				frameTable[i][0] = pageNumber; //page number
				frameTable[i][1] = processNumber; //proess this page belongs
				frameTable[i][2] = currentTime;//load at current Time
				return;
			}
		}
		//Process the evicted process page: add evictoin time by one and add its totla resident timie
		//finid the evicted by generating random number
		int randomNumber = random.nextInt();
		int evictedFrameNum = randomNumber % numFrames;
		int evictedProcessNum = frameTable[evictedFrameNum][1];
		
		//get the evicted process
		Process evictedProcess = processes[evictedProcessNum-1];
		evictedProcess.increaseEvictTimes(); //evicted ones
		
		int loadTime = frameTable[evictedFrameNum][2];
		int residencyTime = currentTime - loadTime;
		evictedProcess.addResidency(residencyTime);//has been residence of such long time
		
		frameTable[evictedFrameNum][0] = pageNumber;
		frameTable[evictedFrameNum][1] = processNumber;
		frameTable[evictedFrameNum][2] = currentTime;
	}

}
