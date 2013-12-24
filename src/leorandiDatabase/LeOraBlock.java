package leorandiDatabase;

class NoFreeSpaceException extends Exception{};

interface IBlock{
	public int getRowsCount();
	public IRow getRow(int index);
	public int getFreeRowsCount();
	public void insertRow(String[] data) throws NoFreeSpaceException;
	public int getRowIndexForBulkInsert();
}

public class LeOraBlock implements IBlock{
	private LeOraRow[] rows = new LeOraRow[LeOraConstants.ROWS_COUNT_IN_ONE_BLOCK];
	private int freeRowsCount = LeOraConstants.ROWS_COUNT_IN_ONE_BLOCK;
	private int rowIndexForBulkInsert = -1;
	
	public int getRowIndexForBulkInsert(){
		int len = rows.length, currentPosition = rowIndexForBulkInsert;
		while(++rowIndexForBulkInsert != currentPosition){
			if(rowIndexForBulkInsert == len){
				rowIndexForBulkInsert = 0;
			}
			if(!rows[rowIndexForBulkInsert].exists()){
				break;
			}
		}
		if((rowIndexForBulkInsert == currentPosition) && (rows[rowIndexForBulkInsert].exists())){
			rowIndexForBulkInsert = -1;
		}
		return rowIndexForBulkInsert;
	}
	
	public LeOraBlock(int columnsCount){
		for(int i=0; i<rows.length; i++){
			rows[i] = new LeOraRow(columnsCount);
		}
		rowIndexForBulkInsert = -1;
	}
	
	public int getRowsCount(){
		return LeOraConstants.ROWS_COUNT_IN_ONE_BLOCK;
	}
	
	public int getFreeRowsCount(){
		return freeRowsCount;
	}
	
	public LeOraRow getRow(int index){
		return rows[index];
	}
	
	public int getMemorySize(){
		return rows.length * rows[0].getMemorySize();
	}
	
	public void insertRow(String[] data) throws NoFreeSpaceException{
		if(0 == freeRowsCount){
			throw new NoFreeSpaceException();
		}
		int position = getRowIndexForBulkInsert();
		if(-1 == position) throw new NoFreeSpaceException();
		rows[position].update(data);
		freeRowsCount--;
	}
}
