package leorandiDatabase;

interface IExtent{
	public int getBlocksCount();
	public int getFreeBlocksCount();
	public IBlock getBlock(int index);
	public int getBlockIndexForBulkInsert();
	public void insertRow(String[] data) throws NoFreeSpaceException;
	public int getMemorySize();
}

public class LeOraExtent implements IExtent{
	private LeOraBlock[] blocks = new LeOraBlock[LeOraConstants.BLOCKS_COUNT_IN_ONE_EXTENT];
	private int freeBlocksCount = LeOraConstants.BLOCKS_COUNT_IN_ONE_EXTENT;
	private int blockIndexForBulkInsert = -1;
	
	public int getBlockIndexForBulkInsert(){
		int len = blocks.length, currentPosition = blockIndexForBulkInsert;
		while(++blockIndexForBulkInsert != currentPosition){
			if(blockIndexForBulkInsert == len){
				blockIndexForBulkInsert = 0;
			}
			if(blocks[blockIndexForBulkInsert].getFreeRowsCount() > 0){
				break;
			}
		}
		if((blockIndexForBulkInsert == currentPosition) && (blocks[blockIndexForBulkInsert].getFreeRowsCount() == 0)){
			blockIndexForBulkInsert = -1;
		}
		return blockIndexForBulkInsert;
	}
	
	public LeOraExtent(int columnsCount){
		for(int i=0; i<blocks.length; i++){
			blocks[i] = new LeOraBlock(columnsCount);
		}
		blockIndexForBulkInsert = 0;
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
	
	public void insertRow(String[] data) throws NoFreeSpaceException{
		if(0 == freeBlocksCount){
			throw new NoFreeSpaceException();
		}
		int position = getBlockIndexForBulkInsert();
		if(-1 == position) throw new NoFreeSpaceException();
		blocks[position].insertRow(data);
	}
}
