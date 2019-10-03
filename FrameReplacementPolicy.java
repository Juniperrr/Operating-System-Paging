package Lab4;

public interface FrameReplacementPolicy {
	
	boolean pageFaultOccured(int pageNumber, int processNumber, int currentTime);
	
	void replacePage(Process[] processes, int pageNumber, int processNumber, int currentTime);
}
