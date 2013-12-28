package leorandiDatabase;

import java.util.ArrayList;

interface ISegment{
	public IExtent getExtent(int index);
	public void addExtent();
	public int getExtentIndexForBulkInsert();
	public int getMemorySize();
	public int getRowsCount();
	public int getFreeRowsCount();
	public int getExtentsCount();
	public int getFreeExtentsCount();
	public void insertRow(String[] data) throws NoFreeSpaceException;
}

public class LeOraSegment implements ISegment{
	private ArrayList<LeOraExtent> extents = new ArrayList<LeOraExtent>();
	private int columnsCount;
	private LeOraField partitionKey = new LeOraField();
	private LeOraStatistics statistics;
	private int extentIndexForBulkInsert = -1;
	private int freeExtentsCount = 0;
	
	public void setPartitionKey(String source){
		this.partitionKey.setData(source);
	}
	
	public String getPartitionKey(){
		return this.partitionKey.getDataAsString();
	}
	
	public LeOraStatistics gatherStatistics(){
		statistics = null;
		LeOraStatistics st = new LeOraStatistics();
		for(int extentIndex=0; extentIndex<getExtentsCount(); extentIndex++){
			LeOraExtent extent = getExtent(extentIndex);
			st.freeBlocks += extent.getFreeBlocksCount();
			st.memoryBlocks += extent.getBlocksCount();
			for(int blockIndex=0; blockIndex<extent.getBlocksCount(); blockIndex++){
				LeOraBlock block = extent.getBlock(blockIndex); 
				st.freeRows += block.getFreeRowsCount();
				st.memoryRows += block.getRowsCount(); 
				block = null;
			}
			extent = null;
		}
		st.freeBytes = st.freeRows * columnsCount * LeOraConstants.VARCHAR_MAX_SIZE;
		st.memoryBytes = st.memoryRows * columnsCount * LeOraConstants.VARCHAR_MAX_SIZE;
		statistics = st;
		return statistics;
	}
	
	public LeOraStatistics getStatistics(){
		return statistics;
	}
	
	public LeOraSegment(int columnsCount){
		this.columnsCount = columnsCount;
		extents.add(new LeOraExtent(columnsCount));
		freeExtentsCount++;
		this.extentIndexForBulkInsert = -1;
	}
	
	public int getExtentsCount(){
		return extents.size();
	}
	
	public int getFreeExtentsCount(){
		return freeExtentsCount;
	}
	
	public LeOraExtent getExtent(int index){
		return extents.get(index);
	}
	
	public void addExtent(){
		extents.add(new LeOraExtent(columnsCount));
	}
	
	public int getColumnsCount(){
		return columnsCount;
	}
	
	public int getMemorySize(){
		return extents.size() * extents.get(0).getMemorySize();
	}
	
	public int getRowsCount(){
		int res = 0;
		for(int i=0; i<getExtentsCount(); i++){
			res += extents.get(i).getRowsCount();
		}
		return res;
	}
	
	public int getFreeRowsCount(){
		int res = 0;
		for(int i=0; i<getExtentsCount(); i++){
			res += extents.get(i).getFreeRowsCount();
		}
		//LeOraUtils.p("  segment getFreeRowsCount res="+res);
		return res;
	}
	
	public int getExtentIndexForBulkInsert(){
		int len = extents.size(), currentPosition = extentIndexForBulkInsert;
		boolean found = false; // new variable to speed up execution
		do{
			if(extentIndexForBulkInsert == -1){
				extentIndexForBulkInsert = 0;
			}
			if(extentIndexForBulkInsert == len){
				extentIndexForBulkInsert = 0;
			}
			if(extents.get(extentIndexForBulkInsert).getFreeBlocksCount() > 0){
				found = true;
				break;
			}
		}while(++extentIndexForBulkInsert != currentPosition);
		if(!found){
			extentIndexForBulkInsert = -1;
		}
		return extentIndexForBulkInsert;
	}
	
	public void insertRow(String[] data) throws NoFreeSpaceException{
		if(0 == freeExtentsCount){
			addExtent();
			freeExtentsCount++;
			//throw new NoFreeSpaceException();
		}
		int position = getExtentIndexForBulkInsert();
		if(-1 == position) throw new NoFreeSpaceException();
		LeOraExtent e = extents.get(position);
		e.insertRow(data);
		// if extent does not no more have free blocks then decreases free extents count 
		if(0 == e.getFreeBlocksCount())freeExtentsCount--;
	}
}
