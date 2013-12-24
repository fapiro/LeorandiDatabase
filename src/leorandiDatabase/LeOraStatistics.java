package leorandiDatabase;

public class LeOraStatistics {
	public int memoryBlocks=0, memoryRows=0, memoryBytes=0;
	public int freeBlocks=0, freeRows=0, freeBytes=0;
	
	public float freePercent(){
		return (freeBytes / memoryBytes) * 100;
	};
	
	public LeOraStatistics addFrom(LeOraStatistics addThis){
		this.memoryBlocks += addThis.memoryBlocks;
		this.memoryRows += addThis.memoryRows;
		this.memoryBytes += addThis.memoryBytes;
		
		this.freeBlocks += addThis.freeBlocks;
		this.freeRows += addThis.freeRows;
		this.freeBytes += addThis.freeBytes;
		
		return this;
	}
}
