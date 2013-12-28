package leorandiDatabase;

interface IExtent{
	public IBlock getBlock(int index);
	public int getBlockIndexForBulkInsert();
	public int getMemorySize();
	public int getRowsCount();
	public int getFreeRowsCount();
	public int getBlocksCount();
	public int getFreeBlocksCount();
	public void insertRow(String[] data) throws NoFreeSpaceException;
}

public class LeOraExtent implements IExtent{
	private LeOraBlock[] blocks = new LeOraBlock[LeOraConstants.BLOCKS_COUNT_IN_ONE_EXTENT];
	private int freeBlocksCount = LeOraConstants.BLOCKS_COUNT_IN_ONE_EXTENT;
	private int blockIndexForBulkInsert = -1;
	
	public int getBlockIndexForBulkInsert(){
		int len = blocks.length, currentPosition = blockIndexForBulkInsert;
		boolean found = false; // new variable to speed up execution
		do{
			if(blockIndexForBulkInsert == -1){
				blockIndexForBulkInsert = 0;
			}
			if(blockIndexForBulkInsert == len){
				blockIndexForBulkInsert = 0;
			}
			if(blocks[blockIndexForBulkInsert].getFreeRowsCount() > 0){
				found = true;
				break;
			}
		}while(++blockIndexForBulkInsert != currentPosition);
		if(!found){
			blockIndexForBulkInsert = -1;
		}
		return blockIndexForBulkInsert;
	}
	
	public LeOraExtent(int columnsCount){
		for(int i=0; i<blocks.length; i++){
			blocks[i] = new LeOraBlock(columnsCount);
		}
		blockIndexForBulkInsert = -1;
	}
	
	public int getBlocksCount(){
		return LeOraConstants.BLOCKS_COUNT_IN_ONE_EXTENT;
	}
	
	public int getFreeBlocksCount(){
		return freeBlocksCount;
	}
	
	public LeOraBlock getBlock(int index){
		return blocks[index];
	}
	
	public int getMemorySize(){
		return blocks.length * blocks[0].getMemorySize();
	}
	
	public int getRowsCount(){
		int res = 0;
		for(int i=0; i<getBlocksCount(); i++){
			res += blocks[i].getRowsCount();
		}
		return res;
	}
	
	public int getFreeRowsCount(){
		int res = 0;
		for(int i=0; i<getBlocksCount(); i++){
			res += blocks[i].getFreeRowsCount();
			/*if(blocks[i].getRowsCount() != blocks[i].getFreeRowsCount()){
				LeOraUtils.p(" for extent this block i="+i+" contains rows count="+(blocks[i].getRowsCount() - blocks[i].getFreeRowsCount()));
			}*/
		}
		//LeOraUtils.p("  extent getFreeRowsCount res="+res);
		return res;
	}
	
	public void insertRow(String[] data) throws NoFreeSpaceException{
		if(0 == freeBlocksCount){
			throw new NoFreeSpaceException();
		}
		int position = getBlockIndexForBulkInsert();
		if(-1 == position) throw new NoFreeSpaceException();
		//LeOraUtils.p("extent insertRow blocks[position] where position="+position);
		LeOraBlock b = blocks[position];
		b.insertRow(data);
		if(0 == b.getFreeRowsCount())freeBlocksCount--;
	}
}
