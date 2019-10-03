package Lab4;


public class FramePolicyLRU implements FrameReplacementPolicy {
	int numFrames;
	int [][] frameTable; //each frame contains three information:page number, process this page belongs and when this frame is put into memory
	
	public FramePolicyLRU(int machineSize, int pageSize) {
		this.numFrames = machineSize / pageSize;
		this.frameTable = new int[numFrames][4]; //Stores page number, process number, load time, last recently accessed time
	}

	@Override
	public boolean pageFaultOccured(int pageNumber, int processNumber, int currentTime) {
		for (int i = 0; i < numFrames; i++) {
			//if the demanding page is in the frame table then obviously o page fault occurs
			if ((frameTable[i][0] == pageNumber) && frameTable[i][1] == processNumber) {
				frameTable[i][2] = currentTime; //Fine destination page then modify to the 
				return false;
			}
		}
		//Cannot find the demanding page, so page fault occurred.
		return true; 
	}

	@Override
	public void replacePage(Process[] processes, int pageNumber, int processNumber, int currentTime) {
		int lastAccessedTime = currentTime;
		int replacedFrame = 0;
		
		for (int i = (numFrames - 1); i >= 0; i--) {
			//if there was an unusedframe, use that frame element and end searching, searching begins from the highest address
			if ((frameTable[i][0] == 0) && (frameTable[i][1] == 0)) {
				frameTable[i][0] = pageNumber; //page number
				frameTable[i][1] = processNumber; //proess this page belongs
				frameTable[i][2] = currentTime; //least recently time
				frameTable[i][3] = currentTime;//load at current Time
			}
			//Find the least recently used frame, whose recent time should be the largest to current
			else if (lastAccessedTime > frameTable[i][2]) {
				replacedFrame = i;
				lastAccessedTime = frameTable[i][2];
			}
		}
		
		//process the evicted process page; add eviction time by one, and add its total resident time
		int evictedProcessNumber = frameTable[replacedFrame][1];
		//Get the process which was evicted
		Process evictedProcess = processes[evictedProcessNumber - 1];
		evictedProcess.increaseEvictTimes();
		
		//add resident time
		int loadTime = frameTable[replacedFrame][3];
		int residencyTime = currentTime - loadTime;
		evictedProcess.addResidency(residencyTime);
		
		//Put the new page into the destinatiton frame
		frameTable[replacedFrame][0] = pageNumber;
		frameTable[replacedFrame][1] = processNumber;
		frameTable[replacedFrame][2] = currentTime;
		frameTable[replacedFrame][3] = currentTime;
	}

}
