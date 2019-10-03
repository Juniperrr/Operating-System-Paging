package Lab4;

import java.util.ArrayList;

public class FramePolicyFIFO implements FrameReplacementPolicy {
	int numFrames;
	ArrayList<int[]> frameTable; //each frame contains three information:page number, process this page belongs and when this frame is put into memory
	
	public FramePolicyFIFO(int machineSize, int pageSize) {
		this.numFrames = machineSize / pageSize;
		this.frameTable = new ArrayList<int[]>();
	}

	@Override
	public boolean pageFaultOccured(int pageNumber, int processNumber, int currentTime) {
		for (int i = 0; i < frameTable.size(); i++) {
			int[] framePage = frameTable.get(i);
			//if the demanding page is in the fram table then obviously no page
			if ((framePage[0] == pageNumber) && framePage[1] == processNumber) {
				return false;
			}
		}
		//if cannot find the demanding page, return false
		return true;
	}

	@Override
	public void replacePage(Process[] processes, int pageNumber, int processNumber, int currentTime) {
		if (numFrames == frameTable.size()) {
			int[] evictedFrame = frameTable.get(0);
			int evictedProcessNumber = evictedFrame[1];
			//get the evicted process
			Process evictedProcess = processes[evictedProcessNumber - 1];
			evictedProcess.increaseEvictTimes();
			
			//add total resident time fo the evicted process
			int loadTime = evictedFrame[2];
			int residencyTime = currentTime - loadTime;
			evictedProcess.addResidency(residencyTime);
			//rermove the first process in the queue
			frameTable.remove(0);
		}
		//add new demamding page to the first in first out queue
		int[] replacedFrame = {pageNumber, processNumber, currentTime};
		frameTable.add(replacedFrame);
		
	}
	
}
